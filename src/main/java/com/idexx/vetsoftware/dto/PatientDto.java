package com.idexx.vetsoftware.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PatientDto {

    private String eventType;
    
    private Long patientId;
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    @NotBlank(message = "Species is required")
    @Size(max = 50, message = "Species must not exceed 50 characters")
    private String species;
    
    @NotBlank(message = "Breed is required")
    @Size(max = 100, message = "Breed must not exceed 100 characters")
    private String breed;

    @Pattern(regexp = "^[0-9]{15}$", message = "Microchip ID must be exactly 15 digits")
    private String microchipId;

	@NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Unknown)$", message = "Gender must be Male, Female, or Unknown")
    private String gender;
    
    // All-args constructor
    public PatientDto(String eventType, Long patientId, String name, Long ownerId) {
        this.eventType = eventType;
        this.patientId = patientId;
        this.name = name;
        this.ownerId = ownerId;
    }
    
    // Getter and Setter for eventType
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    // Getter and Setter for patientId
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    // Getter and Setter for patientName
    public String getPatientName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for ownerId
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getBreed() {
		return breed;
	}

	public void setBreed(String breed) {
		this.breed = breed;
	}

	public String getMicrochipId() {
		return microchipId;
	}

	public void setMicrochipId(String microchipId) {
		this.microchipId = microchipId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}