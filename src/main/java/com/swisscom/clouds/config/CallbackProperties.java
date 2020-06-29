package com.swisscom.clouds.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("callback")
public class CallbackProperties {

    private String msTeamsUri;

}
