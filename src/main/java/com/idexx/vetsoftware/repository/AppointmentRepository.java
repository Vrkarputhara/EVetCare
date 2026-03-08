package com.idexx.vetsoftware.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.idexx.vetsoftware.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    
    List<Appointment> findByVetId(Long vetId);
    
    List<Appointment> findByStatus(String status);
    
    @Query("SELECT a FROM Appointment a WHERE a.startTime BETWEEN :start AND :end")
    List<Appointment> findByDateRange(LocalDateTime start, LocalDateTime end);
}