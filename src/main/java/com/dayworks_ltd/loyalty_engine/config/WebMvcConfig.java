package com.dayworks_ltd.loyalty_engine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebMvcConfig {//implements WebMvcConfigurer {

//    @Value("${client.url}")
    private String clientUrl;

//    @Override
    public void addCorsMappings(CorsRegistry corsRegistry)
    {
        corsRegistry
                .addMapping("/**")
//                .allowedOrigins(clientUrl)
                .allowedOrigins("http://localhost:3000")
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}
