package com.idexx.vetsoftware.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Component
public class CustomMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter patientCreatedCounter;
    private final Counter appointmentCreatedCounter;
    private final Timer appointmentProcessingTimer;
    
    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.patientCreatedCounter = Counter.builder("patients.created")
            .description("Number of patients created")
            .register(meterRegistry);
        
        this.appointmentCreatedCounter = Counter.builder("appointments.created")
            .description("Number of appointments created")
            .register(meterRegistry);
        
        this.appointmentProcessingTimer = Timer.builder("appointments.processing.time")
            .description("Time taken to process appointments")
            .register(meterRegistry);
    }
    
    public void incrementPatientCreated() {
        patientCreatedCounter.increment();
    }
    
    public void incrementAppointmentCreated() {
        appointmentCreatedCounter.increment();
    }
    
    public Timer.Sample startAppointmentProcessing() {
        return Timer.start(meterRegistry);
    }
    
    public void stopAppointmentProcessing(Timer.Sample sample) {
        sample.stop(appointmentProcessingTimer);
    }
}