package com.wantedbackendassignment.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private String version;
    private String title;

    @Bean
    public OpenAPI apiV1() {
        version = "v1";
        title = "wanted preonboarding assignment";

        return new OpenAPI()
                .info(getInfo(version, title))
                .components(getComponents());
    }

    private Components getComponents() {
        return new Components();
    }

    private Info getInfo(String version, String title) {
        return new Info()
                .version(version)
                .title(title)
                .description("원티드 백엔드 프리온보딩 과제 API 문서입니다");
    }
}
