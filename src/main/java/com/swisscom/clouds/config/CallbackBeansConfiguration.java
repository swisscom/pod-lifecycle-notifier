package com.swisscom.clouds.config;

import com.swisscom.clouds.callbacks.Callback;
import com.swisscom.clouds.callbacks.console.ConsoleLogger;
import com.swisscom.clouds.callbacks.teams.MsTeams;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CallbackBeansConfiguration {

    private final CallbackProperties callbackProperties;
    private final KubernetesProperties kubernetesProperties;
    private final BuildProperties buildProperties;
    private final WebClient.Builder webclientBuilder;

    @Bean
    public List<Callback> createCallbackBeans() {
        List<Callback> callbackList = new ArrayList<>();
        callbackList.add(new ConsoleLogger(kubernetesProperties, buildProperties));

        if (StringUtils.isNotBlank(callbackProperties.getMsTeamsUri())) {
            callbackList.add(new MsTeams(callbackProperties, kubernetesProperties, webclientBuilder, buildProperties));
        }

        return callbackList;
    }

}
