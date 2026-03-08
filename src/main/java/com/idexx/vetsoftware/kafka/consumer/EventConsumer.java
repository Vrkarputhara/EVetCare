package com.idexx.vetsoftware.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idexx.vetsoftware.dto.AppointmentDto;
import com.idexx.vetsoftware.dto.PatientDto;

@Service
public class EventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    
    @KafkaListener(topics = "patient-events", groupId = "vet-software-group")
    public void consumePatientEvent(String event) {
        logger.info("Consumed patient event: {}", event);
        
        // Process the event (e.g., update search index, send notifications, etc.)
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PatientDto patientEvent = objectMapper.readValue(event, PatientDto.class);
            
            // Handle different event types
            switch (patientEvent.getEventType()) {
                case "PATIENT_CREATED":
                    logger.info("New patient created: {}", patientEvent.getPatientId());
                    // Additional logic for patient creation
                    break;
                case "PATIENT_UPDATED":
                    logger.info("Patient updated: {}", patientEvent.getPatientId());
                    // Additional logic for patient update
                    break;
                case "PATIENT_DELETED":
                    logger.info("Patient deleted: {}", patientEvent.getPatientId());
                    // Additional logic for patient deletion
                    break;
                default:
                    logger.warn("Unknown patient event type: {}", patientEvent.getEventType());
            }
        } catch (Exception e) {
            logger.error("Error processing patient event", e);
        }
    }
    
    @KafkaListener(topics = "appointment-events", groupId = "vet-software-group")
    public void consumeAppointmentEvent(String event) {
        logger.info("Consumed appointment event: {}", event);
        
        // Process the event (e.g., update calendar, send reminders, etc.)
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AppointmentDto appointmentEvent = objectMapper.readValue(event, AppointmentDto.class);
            
            // Handle different event types
            switch (appointmentEvent.getEventType()) {
                case "APPOINTMENT_CREATED":
                    logger.info("New appointment created: {}", appointmentEvent.getAppointmentId());
                    // Additional logic for appointment creation
                    break;
                case "APPOINTMENT_UPDATED":
                    logger.info("Appointment updated: {}", appointmentEvent.getAppointmentId());
                    // Additional logic for appointment update
                    break;
                case "APPOINTMENT_DELETED":
                    logger.info("Appointment deleted: {}", appointmentEvent.getAppointmentId());
                    // Additional logic for appointment deletion
                    break;
                default:
                    logger.warn("Unknown appointment event type: {}", appointmentEvent.getEventType());
            }
        } catch (Exception e) {
            logger.error("Error processing appointment event", e);
        }
    }
}