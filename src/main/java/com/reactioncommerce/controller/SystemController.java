package com.reactioncommerce.controller;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * GraphQL controller for system-level operations.
 */
@Controller
public class SystemController {

    private final Instant startTime = Instant.now();

    /**
     * Simple ping query for health checks.
     */
    @QueryMapping
    public Mono<String> ping() {
        return Mono.just("pong");
    }

    /**
     * Get system information.
     */
    @QueryMapping
    public Mono<Map<String, Object>> systemInformation() {
        Duration uptime = Duration.between(startTime, Instant.now());
        
        return Mono.just(Map.of(
            "version", "1.0.0",
            "uptime", formatDuration(uptime),
            "environment", System.getProperty("spring.profiles.active", "default")
        ));
    }

    /**
     * Echo mutation for testing.
     */
    @MutationMapping
    public Mono<String> echo(@Argument String message) {
        return Mono.just("Echo: " + message);
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
