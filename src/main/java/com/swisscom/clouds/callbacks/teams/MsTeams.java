package com.swisscom.clouds.callbacks.teams;

import com.swisscom.clouds.callbacks.Callback;
import com.swisscom.clouds.callbacks.teams.entities.Severity;
import com.swisscom.clouds.config.CallbackProperties;
import com.swisscom.clouds.config.KubernetesProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MsTeams implements Callback {

    private final WebClient webClient;
    private final KubernetesProperties k8sProperties;
    private final BuildProperties buildProperties;

    public MsTeams(CallbackProperties callbackProperties, KubernetesProperties k8sProperties, WebClient.Builder webclientBuilder, BuildProperties buildProperties) {
        this.k8sProperties = k8sProperties;
        this.buildProperties = buildProperties;
        this.webClient = webclientBuilder.baseUrl(callbackProperties.getMsTeamsUri()).build();
    }

    @Override
    public Mono<?> onStartup() {
        return sendMessage("Application Startup", Severity.SUCCESS);
    }

    @Override
    public Mono<?> onShutdown() {
        return sendMessage("Application Shutdown", Severity.WARN);
    }

    private Mono<?> sendMessage(String title, Severity severity) {
        Map<String, Object> messageCard = createMessageCard(title, severity);
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(messageCard)
                .exchangeToMono(this::logResponseStatusAndBody);

    }

    private Map<String, Object> createMessageCard(String title, Severity severity) {
        Map<String, Object> messageCard = new HashMap<>();

        messageCard.put("@context", "https://schema.org/extensions");
        messageCard.put("@type", "MessageCard");
        messageCard.put("title", title);
        messageCard.put("themeColor", severity.getCode());
        messageCard.put("text", "Pod Lifecycle Notifier triggered notification.");
        messageCard.put("potentialAction", List.of());

        List<Map<String, Object>> facts = new ArrayList<>();

        if (StringUtils.isNotBlank(k8sProperties.getCluster())) {
            facts.add(Map.of("name", "Cluster:", "value", getOrEmpty(k8sProperties.getCluster())));
        }
        facts.add(Map.of("name", "Node:", "value", getOrEmpty(k8sProperties.getNode())));
        facts.add(Map.of("name", "Node IP Address:", "value", getOrEmpty(k8sProperties.getNodeIpAddress())));
        facts.add(Map.of("name", "Namespace:", "value", getOrEmpty(k8sProperties.getNamespace())));
        facts.add(Map.of("name", "Pod:", "value", getOrEmpty(k8sProperties.getPod())));
        facts.add(Map.of("name", "Pod IP Address:", "value", getOrEmpty(k8sProperties.getPodIpAddress())));
        facts.add(Map.of("name", "Service Account:", "value", getOrEmpty(k8sProperties.getServiceAccount())));
        facts.add(Map.of("name", "Build Version:", "value", getOrEmpty(buildProperties.getVersion())));

        Map<String, Object> factSet = Map.of("facts", facts);
        List<Map<String, Object>> sections = List.of(factSet);
        messageCard.put("sections", sections);

        return messageCard;
    }

    private String getOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private Mono<?> logResponseStatusAndBody(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .defaultIfEmpty("[empty body]")
                .flatMap(body -> Mono.fromRunnable(() -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        log.info("Received status={} from MS Teams API", clientResponse.statusCode().toString());
                    } else {
                        log.error("Received status={} from MS Teams API with responseBody={}", clientResponse.statusCode().toString(), body);
                    }
                }));
    }

}
