package com.swisscom.clouds.callbacks.teams;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Slf4j
public class WebClientHelper {

    public static Mono<?> logResponseBodyAndReturnEmpty(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .defaultIfEmpty("[empty body]")
                .flatMap(body -> Mono.fromRunnable(() -> logResponseBody(clientResponse.statusCode(), body)));
    }

    private static void logResponseBody(HttpStatus httpStatus, String body) {
        if (httpStatus != HttpStatus.NOT_FOUND) {
            log.error("Received status={} from MS Teams API with responseBody={}", httpStatus.toString(), body);
        } else {
            log.warn("Received status={} from MS Teams API", httpStatus.toString());
        }
    }
}
