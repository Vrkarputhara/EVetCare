package com.idexx.vetsoftware.service;

import java.util.List;
import java.util.Optional;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idexx.vetsoftware.dto.AppointmentDto;
import com.idexx.vetsoftware.kafka.producer.EventProducer;
import com.idexx.vetsoftware.model.Appointment;
import com.idexx.vetsoftware.repository.AppointmentRepository;
import com.idexx.vetsoftware.repository.PatientRepository;
import com.idexx.vetsoftware.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final EventProducer eventProducer;
    
    @Autowired
    public AppointmentService(
        AppointmentRepository appointmentRepository, 
        PatientRepository patientRepository,
        UserRepository userRepository,
        @Autowired(required = false) EventProducer eventProducer
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.eventProducer = eventProducer;
    }
    
    public Appointment saveAppointment(Appointment appointment) {
        // Validate patient and vet exist
        if (!patientRepository.existsById(appointment.getPatient().getId())) {
            throw new ResourceNotFoundException("Patient not found");
        }
        
        if (!userRepository.existsById(appointment.getVet().getId())) {
            throw new ResourceNotFoundException("Vet not found");
        }
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Send event to Kafka
        AppointmentDto event = new AppointmentDto(
            "APPOINTMENT_CREATED", 
            savedAppointment.getId(), 
            savedAppointment.getPatient().getId(),
            savedAppointment.getVet().getId(),
            savedAppointment.getStartTime()
        );
        if (eventProducer != null) {
        	eventProducer.sendAppointmentEvent(event);
        }
        
        return savedAppointment;
    }
    
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }
    
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
    
    public List<Appointment> getAppointmentsByVetId(Long vetId) {
        return appointmentRepository.findByVetId(vetId);
    }
    
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
        
        // Send event to Kafka
        AppointmentDto event = new AppointmentDto(
            "APPOINTMENT_DELETED", 
            id, 
            null,
            null,
            null
        );
        if (eventProducer != null) {
        	eventProducer.sendAppointmentEvent(event);
        }
    }
    
    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        return appointmentRepository.findById(id)
            .map(appointment -> {
                appointment.setPatient(appointmentDetails.getPatient());
                appointment.setVet(appointmentDetails.getVet());
                appointment.setStartTime(appointmentDetails.getStartTime());
                appointment.setEndTime(appointmentDetails.getEndTime());
                appointment.setStatus(appointmentDetails.getStatus());
                appointment.setNotes(appointmentDetails.getNotes());
                appointment.setCost(appointmentDetails.getCost());
                
                Appointment updatedAppointment = appointmentRepository.save(appointment);
                
                // Send event to Kafka
                AppointmentDto event = new AppointmentDto(
                    "APPOINTMENT_UPDATED", 
                    updatedAppointment.getId(), 
                    updatedAppointment.getPatient().getId(),
                    updatedAppointment.getVet().getId(),
                    updatedAppointment.getStartTime()
                );
                if (eventProducer != null) {
                	eventProducer.sendAppointmentEvent(event);
                }
                
                return updatedAppointment;
            })
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }
}