package com.wantedbackendassignment.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.wantedbackendassignment.api.properties")
public class WantedPreOnboardingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WantedPreOnboardingBackendApplication.class, args);
    }

}
