package com.swisscom.clouds.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("kubernetes")
public class KubernetesProperties {

    private String cluster;
    private String node;
    private String namespace;
    private String pod;
    private String serviceAccount;
    private String podIpAddress;
    private String nodeIpAddress;

}
