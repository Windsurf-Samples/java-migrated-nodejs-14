package com.reactioncommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

/**
 * MongoDB configuration for reactive operations.
 */
@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "reaction";
    }
}
