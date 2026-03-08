package com.idexx.vetsoftware.service;

import java.util.List;
import java.util.Optional;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.idexx.vetsoftware.dto.PatientDto;
import com.idexx.vetsoftware.kafka.producer.EventProducer;
import com.idexx.vetsoftware.metrics.CustomMetrics;
import com.idexx.vetsoftware.model.Patient;
import com.idexx.vetsoftware.repository.PatientRepository;


import jakarta.transaction.Transactional;

@Service
@Transactional
public class PatientService {
    private final PatientRepository patientRepository;
    private final EventProducer eventProducer;
    private final CustomMetrics customMetrics;

    
    @Autowired
    public PatientService(PatientRepository patientRepository, EventProducer eventProducer, CustomMetrics customMetrics) {
        this.patientRepository = patientRepository;
        this.eventProducer = eventProducer;
        this.customMetrics = customMetrics;
    }
    
    @CacheEvict(value = "patients", allEntries = true)
    public Patient savePatient(Patient patient) {
        Patient savedPatient = patientRepository.save(patient);
        
        customMetrics.incrementPatientCreated();
        
        // Send event to Kafka
        PatientDto event = new PatientDto(
            "PATIENT_CREATED", 
            savedPatient.getId(), 
            savedPatient.getName(),
            savedPatient.getOwner().getId()
        );
        eventProducer.sendPatientEvent(event);
        
        return savedPatient;
    }
    
    @Cacheable(value = "patients", key = "#id")
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }
    
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
    
    @Cacheable(value = "patients", key = "'owner-' + #ownerId")
    public List<Patient> getPatientsByOwnerId(Long ownerId) {
        return patientRepository.findByOwnerId(ownerId);
    }
    
    @CacheEvict(value = "patients", key = "#id")
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
        
        // Send event to Kafka
        PatientDto event = new PatientDto(
            "PATIENT_DELETED", 
            id, 
            null,
            null
        );
        eventProducer.sendPatientEvent(event);
    }
    
    @CacheEvict(value = "patients", key = "#id")
    public Patient updatePatient(Long id, Patient patientDetails) {
        return patientRepository.findById(id)
            .map(patient -> {
                patient.setName(patientDetails.getName());
                patient.setSpecies(patientDetails.getSpecies());
                patient.setBreed(patientDetails.getBreed());
                patient.setBirthDate(patientDetails.getBirthDate());
                patient.setGender(patientDetails.getGender());
                patient.setColor(patientDetails.getColor());
                patient.setMicrochipId(patientDetails.getMicrochipId());
                
                Patient updatedPatient = patientRepository.save(patient);
                
                // Send event to Kafka
                PatientDto event = new PatientDto(
                    "PATIENT_UPDATED", 
                    updatedPatient.getId(), 
                    updatedPatient.getName(),
                    updatedPatient.getOwner().getId()
                );
                eventProducer.sendPatientEvent(event);
                
                return updatedPatient;
            })
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }
}