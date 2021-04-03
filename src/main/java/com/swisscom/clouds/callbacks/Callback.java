package com.swisscom.clouds.callbacks;

import reactor.core.publisher.Mono;

public interface Callback {

    default String getDescription() {
        return getClass().getSimpleName();
    }

    Mono<?> onStartup();

    Mono<?> onShutdown();

}
