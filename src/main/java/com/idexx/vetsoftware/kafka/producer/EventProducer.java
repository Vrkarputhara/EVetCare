package com.idexx.vetsoftware.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idexx.vetsoftware.dto.AppointmentDto;
import com.idexx.vetsoftware.dto.PatientDto;
import com.idexx.vetsoftware.dto.UserInfoResponse;

@Service
@Profile("!Prod")
public class EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void sendPatientEvent(PatientDto event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String eventJson = objectMapper.writeValueAsString(event);
            
            logger.info("Sending patient event: {}", eventJson);
            kafkaTemplate.send("patient-events", eventJson);
        } catch (Exception e) {
            logger.error("Error sending patient event", e);
        }
    }
    
    public void sendAppointmentEvent(AppointmentDto event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String eventJson = objectMapper.writeValueAsString(event);
            
            logger.info("Sending appointment event: {}", eventJson);
            kafkaTemplate.send("appointment-events", eventJson);
        } catch (Exception e) {
            logger.error("Error sending appointment event", e);
        }
    }
    
    public void sendUserEvent(UserInfoResponse event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String eventJson = objectMapper.writeValueAsString(event);

            logger.info("Sending user event: {}", eventJson);
            kafkaTemplate.send("user-events", eventJson);
        } catch (Exception e) {
            logger.error("Error sending user event", e);
        }
    }
}