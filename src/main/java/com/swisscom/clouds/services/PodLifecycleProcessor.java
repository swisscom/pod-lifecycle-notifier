package com.swisscom.clouds.services;

import com.swisscom.clouds.callbacks.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

    @EventListener(ApplicationStartedEvent.class)
    public void applicationStarted() {
        Flux.fromIterable(callbackImplementations)
                .flatMap(Callback::onStartup)
                .subscribe();
    }

    @EventListener(ContextClosedEvent.class)
    public void contextClosed() {
        Flux.fromIterable(callbackImplementations)
                .flatMap(Callback::onShutdown)
                .blockLast();
    }

}
