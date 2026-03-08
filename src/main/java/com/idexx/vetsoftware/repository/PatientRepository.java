package com.idexx.vetsoftware.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.idexx.vetsoftware.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByOwnerId(Long ownerId);
    
    @Query("SELECT p FROM Patient p WHERE p.name LIKE %:name%")
    List<Patient> findByNameContaining(String name);
    
    @Query("SELECT p FROM Patient p WHERE p.microchipId = :microchipId")
    Optional<Patient> findByMicrochipId(String microchipId);
}
