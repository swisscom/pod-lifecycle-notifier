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

    @Bean
    public KubernetesProperties kubernetesProperties(KubernetesProperties kubernetesProperties) {

        FileSystemResource resourceFile = new FileSystemResource("/mnt/init-container/kubeapi-version");
        if (resourceFile.exists()) {
            try {
                InputStream resource = resourceFile.getInputStream();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
                    String apiVersion = reader.readLine();
                    if (StringUtils.isNotBlank(apiVersion)) {
                        log.info("Read apiVersion={}", apiVersion);
                        kubernetesProperties.setApiVersion(apiVersion);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return kubernetesProperties;
    }

}
