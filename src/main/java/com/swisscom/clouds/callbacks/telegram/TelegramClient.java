package com.swisscom.clouds.callbacks.telegram;

import com.swisscom.clouds.callbacks.Callback;
import com.swisscom.clouds.config.CallbackProperties;
import com.swisscom.clouds.config.KubernetesProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class TelegramClient implements Callback {

    private final WebClient webClient;
    private final KubernetesProperties k8sProperties;
    private final BuildProperties buildProperties;
    private final String botToken;
    private final String chatId;

    public TelegramClient(CallbackProperties callbackProperties, KubernetesProperties k8sProperties, WebClient.Builder webclientBuilder, BuildProperties buildProperties) {
        this.k8sProperties = k8sProperties;
        this.buildProperties = buildProperties;
        this.botToken = callbackProperties.getTelegramBotToken();
        this.chatId = callbackProperties.getTelegramChatId();
        this.webClient = webclientBuilder.baseUrl("https://api.telegram.org").build();
    }

    @Override
    public Mono<?> onStartup() {
        return sendMessage("Pod-Lifecycle: Startup \uD83D\uDEEB");
    }

    @Override
    public Mono<?> onShutdown() {
        return sendMessage("Pod-Lifecycle: Shutdown \uD83D\uDEEC");
    }

    private Mono<?> sendMessage(String title) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(title).append("\n").append("\n");

        if (StringUtils.isNotBlank(k8sProperties.getApiVersion())) {
            stringBuilder.append("*API Version:* ").append(k8sProperties.getApiVersion()).append("\n");
        }

        if (StringUtils.isNotBlank(k8sProperties.getCluster())) {
            stringBuilder.append("*Cluster:* ").append(k8sProperties.getCluster()).append("\n");
        }

        stringBuilder.append("*Node:* ").append(k8sProperties.getNode()).append("\n");
        stringBuilder.append("*Node IP Address:* ").append(k8sProperties.getNodeIpAddress()).append("\n");
        stringBuilder.append("*Namespace:* ").append(k8sProperties.getNamespace()).append("\n");
        stringBuilder.append("*Pod:* ").append(k8sProperties.getPod()).append("\n");
        stringBuilder.append("*Pod IP Address:* ").append(k8sProperties.getPodIpAddress()).append("\n");
        stringBuilder.append("*Service Account:* ").append(k8sProperties.getServiceAccount()).append("\n");
        stringBuilder.append("*Build Version:* ").append(buildProperties.getVersion()).append("\n");
        String text = stringBuilder.toString();

        Map<String, String> message = Map.of(
                "chat_id", this.chatId,
                "text", text,
                "parse_mode", "Markdown"
        );

        return webClient.post()
                .uri("bot{botToken}/sendMessage", this.botToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .exchangeToMono(this::logResponseStatusAndBody);

    }

    private Mono<?> logResponseStatusAndBody(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .defaultIfEmpty("[empty body]")
                .flatMap(body -> Mono.fromRunnable(() -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        log.info("Received status={} from Telegram API", clientResponse.statusCode().toString());
                    } else {
                        log.error("Received status={} from Telegram API with responseBody={}", clientResponse.statusCode().toString(), body);
                    }
                }));
    }

}
