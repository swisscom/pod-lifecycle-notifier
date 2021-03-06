package com.swisscom.clouds.callbacks.teams;

import com.swisscom.clouds.callbacks.Callback;
import com.swisscom.clouds.callbacks.teams.entities.Fact;
import com.swisscom.clouds.callbacks.teams.entities.FactSet;
import com.swisscom.clouds.callbacks.teams.entities.MessageCard;
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
        final MessageCard messageCard = createMessageCard(title, severity);
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(messageCard)
                .exchangeToMono(this::logResponseStatusAndBody);
    }

    private MessageCard createMessageCard(String title, Severity severity) {
        MessageCard messageCard = new MessageCard(title);
        messageCard.setThemeColor(severity.getCode());
        messageCard.setText("Pod Lifecycle Notifier triggered notification.");
        FactSet factSet = new FactSet();
        if (StringUtils.isNotBlank(k8sProperties.getCluster())) {
            factSet.getFacts().add(new Fact("Cluster:", k8sProperties.getCluster()));
        }
        factSet.getFacts().add(new Fact("Node:", k8sProperties.getNode()));
        factSet.getFacts().add(new Fact("Node IP Address:", k8sProperties.getNodeIpAddress()));
        factSet.getFacts().add(new Fact("Namespace:", k8sProperties.getNamespace()));
        factSet.getFacts().add(new Fact("Pod:", k8sProperties.getPod()));
        factSet.getFacts().add(new Fact("Pod IP Address:", k8sProperties.getPodIpAddress()));
        factSet.getFacts().add(new Fact("Service Account:", k8sProperties.getServiceAccount()));
        factSet.getFacts().add(new Fact("Build Version:", buildProperties.getVersion()));
        if (StringUtils.isNotBlank(k8sProperties.getApiVersion())) {
            factSet.getFacts().add(new Fact("API Version:", k8sProperties.getApiVersion()));
        }
        messageCard.getSections().add(factSet);
        return messageCard;
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
