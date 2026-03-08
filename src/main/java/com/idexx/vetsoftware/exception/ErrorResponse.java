package com.idexx.vetsoftware.exception;

import java.util.List;

public class ErrorResponse {
    private int status;
    private String message;
    private List<String> errors;
    private long timestamp;

    public ErrorResponse(int status, String message, List<String> errors, long timestamp) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    // Getters
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}