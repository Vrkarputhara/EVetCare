package com.idexx.vetsoftware.health;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class KafkaHealthIndicator implements HealthIndicator {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaHealthIndicator.class);
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Override
    public Health health() {
        try {
            Properties props = new Properties();
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            
            try (AdminClient adminClient = AdminClient.create(props)) {
                ListTopicsResult result = adminClient.listTopics();
                result.names().get(5, TimeUnit.SECONDS);
                
                return Health.up()
                    .withDetail("status", "Kafka is reachable")
                    .withDetail("bootstrapServers", bootstrapServers)
                    .build();
            }
        } catch (Exception e) {
            logger.error("Kafka health check failed", e);
            return Health.down()
                .withDetail("status", "Kafka is not reachable")
                .withDetail("bootstrapServers", bootstrapServers)
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}