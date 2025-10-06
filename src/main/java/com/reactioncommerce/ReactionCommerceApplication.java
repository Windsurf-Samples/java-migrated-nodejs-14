package com.reactioncommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

/**
 * Main Spring Boot application class for Reaction Commerce API.
 * 
 * This is a headless e-commerce platform built with Spring Boot, MongoDB, and GraphQL.
 * It provides a comprehensive set of APIs for managing products, orders, customers,
 * payments, and all other aspects of an e-commerce system.
 */
@SpringBootApplication
@EnableReactiveMongoAuditing
public class ReactionCommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactionCommerceApplication.class, args);
    }
}
