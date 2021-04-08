package com.swisscom.clouds.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({KubernetesProperties.class})
public class KubernetesPropertiesEnricher {

    public static final String KUBEAPI_VERSION_PATH = "/mnt/init-container/kubeapi-version";

    @Bean
    public KubernetesProperties kubernetesProperties(KubernetesProperties kubernetesProperties) {

        FileSystemResource resourceFile = new FileSystemResource(KUBEAPI_VERSION_PATH);
        if (resourceFile.exists()) {
            try {
                InputStream resource = resourceFile.getInputStream();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
                    String apiVersion = reader.readLine();
                    apiVersion = StringUtils.removeStartIgnoreCase(apiVersion, "v");
                    log.info("Read apiVersion={} from {}", apiVersion, KUBEAPI_VERSION_PATH);
                    if (StringUtils.isNotBlank(apiVersion)) {
                        kubernetesProperties.setApiVersion(apiVersion);
                    }
                }
            } catch (IOException e) {
                log.error("Could not read apiVersion from {}: ", KUBEAPI_VERSION_PATH, e);
            }
        } else {
            log.warn("Kubernetes API Version file expected at {} not found.", KUBEAPI_VERSION_PATH);
        }
        return kubernetesProperties;
    }

}
