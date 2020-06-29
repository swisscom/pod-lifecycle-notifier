package com.swisscom.clouds.callbacks.console;

import com.swisscom.clouds.callbacks.Callback;
import com.swisscom.clouds.config.KubernetesProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsoleLogger implements Callback {

    private final KubernetesProperties k8sProperties;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Mono<?> onStartup() {
        return Mono.fromRunnable(() -> log.info("{}", createMessage("Application Startup")));
    }

    @Override
    public Mono<?> onShutdown() {
        return Mono.fromRunnable(() -> log.info("{}", createMessage("Application Shutdown")));
    }

    private String createMessage(String title) {
        return String.format("%s: node=%s, nodeIpAddress=%s, namespace=%s, pod=%s, podIpAddress=%s, serviceAccount=%s",
                title, k8sProperties.getNode(), k8sProperties.getNodeIpAddress(), k8sProperties.getNamespace(), k8sProperties.getPod(), k8sProperties.getPodIpAddress(), k8sProperties.getServiceAccount());
    }

}
