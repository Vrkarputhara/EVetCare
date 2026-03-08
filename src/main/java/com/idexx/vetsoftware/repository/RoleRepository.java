package com.idexx.vetsoftware.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.idexx.vetsoftware.model.Role;
import com.idexx.vetsoftware.model.Role.ERole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String roleName);
}