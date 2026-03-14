package com.dayworks_ltd.loyalty_engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi loyaltyEngineApi()
    {
        return GroupedOpenApi.builder()
                .group("LoyaltyEngine")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi()
    {
        return new OpenAPI()
                .info(new Info().title("Loyalty Engine").version("1.0"))
                .servers(List.of(new Server().url("http://localhost:8080/swagger-ui").description("local development server")));
    }
}
