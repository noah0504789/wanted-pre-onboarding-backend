package com.wantedbackendassignment.api.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceProperties {

    @NotBlank
    private final String url;

    @NotBlank
    private final String username;

    @NotBlank
    private final String password;

    @NotBlank
    private final String driverClassName;

    public DatasourceProperties(String url, String username, String password, String driverClassName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
    }
}
