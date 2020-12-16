package com.swisscom.clouds.callbacks.console;

import com.swisscom.clouds.callbacks.Callback;
import com.swisscom.clouds.config.KubernetesProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.info.BuildProperties;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class ConsoleLogger implements Callback {

    private final KubernetesProperties k8sProperties;
    private final BuildProperties buildProperties;

    @Override
    public Mono<?> onStartup() {
        return Mono.fromRunnable(() -> log.info("{}", createMessage("Application Startup")));
    }

    @Override
    public Mono<?> onShutdown() {
        return Mono.fromRunnable(() -> log.info("{}", createMessage("Application Shutdown")));
    }

    private String createMessage(String title) {
        return String.format("%s: %snode=%s, nodeIpAddress=%s, namespace=%s, pod=%s, podIpAddress=%s, serviceAccount=%s, buildVersion=%s",
                title, formatOptionalClusterName(), k8sProperties.getNode(), k8sProperties.getNodeIpAddress(), k8sProperties.getNamespace(), k8sProperties.getPod(), k8sProperties.getPodIpAddress(), k8sProperties.getServiceAccount(), buildProperties.getVersion());
    }

    private String formatOptionalClusterName() {
        if (StringUtils.isNotBlank(k8sProperties.getCluster())) {
            return "cluster=" + k8sProperties.getCluster() + ", ";
        } else {
            return "";
        }
    }

}
