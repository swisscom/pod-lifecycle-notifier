package com.swisscom.clouds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class PodLifecycleNotifier {

    public static void main(String[] args) {
        SpringApplication.run(PodLifecycleNotifier.class, args);
    }

}
