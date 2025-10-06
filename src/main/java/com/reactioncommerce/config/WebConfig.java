package com.reactioncommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Web configuration for CORS and other web-related settings.
 */
@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Value("${reaction.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${reaction.cors.allowed-methods}")
    private String[] allowedMethods;

    @Value("${reaction.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${reaction.cors.allow-credentials}")
    private boolean allowCredentials;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials);
    }
}
