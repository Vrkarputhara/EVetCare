package com.idexx.vetsoftware.health;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthIndicator.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                DatabaseMetaData metaData = connection.getMetaData();
                
                return Health.up()
                    .withDetail("database", metaData.getDatabaseProductName())
                    .withDetail("version", metaData.getDatabaseProductVersion())
                    .withDetail("url", metaData.getURL())
                    .build();
            } else {
                return Health.down().withDetail("error", "Database connection is not valid").build();
            }
        } catch (Exception e) {
            logger.error("Database health check failed", e);
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}