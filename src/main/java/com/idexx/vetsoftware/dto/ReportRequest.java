package com.idexx.vetsoftware.dto;

import java.time.LocalDate;
import java.util.Map;

public class ReportRequest {

    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Object> parameters;

    // Default constructor
    public ReportRequest() {
    }

    // All-args constructor
    public ReportRequest(String type, LocalDate startDate, LocalDate endDate, Map<String, Object> parameters) {
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.parameters = parameters;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}