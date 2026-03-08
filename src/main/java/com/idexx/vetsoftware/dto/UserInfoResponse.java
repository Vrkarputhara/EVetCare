package com.idexx.vetsoftware.dto;

import jakarta.validation.constraints.NotBlank;

public class UserInfoResponse {

    private String eventType;
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String username;
    private String email;
    private String password;

    // ✅ REQUIRED for Jackson
    public UserInfoResponse() {
    }

    // existing constructor
    public UserInfoResponse(String eventType, Long id, String firstName, String lastName, String username, String email) {
        this.eventType = eventType;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    // Getters
    public String getEventType() { return eventType; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    // Setters (optional)
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
}