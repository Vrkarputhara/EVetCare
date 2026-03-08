package com.idexx.vetsoftware.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AppointmentDto {

    private String eventType;
    
    private Long appointmentId;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Vet ID is required")
    private Long vetId;
    
    @NotNull(message = "End time is required")
    private LocalDateTime appointmentTime;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
    
    @AssertTrue(message = "End time must be after start time")
    public boolean isEndTimeValid() {
        return startTime == null || endTime == null || endTime.isAfter(startTime);
    }
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(SCHEDULED|IN_PROGRESS|COMPLETED|CANCELLED)$", 
             message = "Status must be SCHEDULED, IN_PROGRESS, COMPLETED, or CANCELLED")
    private String status;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Cost must be positive")
    @Digits(integer = 6, fraction = 2, message = "Cost must have at most 6 digits and 2 decimal places")
    private BigDecimal cost;
    
    // All-args constructor
    public AppointmentDto(String eventType, Long appointmentId, Long patientId, Long vetId, LocalDateTime appointmentTime) {
        this.eventType = eventType;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.vetId = vetId;
        this.appointmentTime = appointmentTime;
    }

    public String getEventType() {
        return eventType;
    }
    
    void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getVetId() {
        return vetId;
    }

    public void setVetId(Long vetId) {
        this.vetId = vetId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}