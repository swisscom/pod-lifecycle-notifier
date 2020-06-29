package com.swisscom.clouds.services;

import com.swisscom.clouds.callbacks.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PodLifecycleProcessor {

    private final List<Callback> callbackImplementations;

    public PodLifecycleProcessor(List<Callback> callbackInterfaces) {

        this.callbackImplementations = callbackInterfaces.stream()
                .filter(Callback::isEnabled)
                .collect(Collectors.toList());

        log.info("Enabled Callbacks: {}", callbackImplementations.stream().map(Callback::getDescription).collect(Collectors.joining(", ")));

    }

    @PostConstruct
    private void postConstruct() {
        Flux.fromIterable(callbackImplementations)
                .flatMap(Callback::onStartup)
                .subscribe();
    }

    @PreDestroy
    private void preDestroy() {
        Flux.fromIterable(callbackImplementations)
                .flatMap(Callback::onShutdown)
                .blockLast();
    }

}
