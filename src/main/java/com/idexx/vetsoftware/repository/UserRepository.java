package com.idexx.vetsoftware.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.idexx.vetsoftware.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
    User findByEmail(String email);
    User findByApiToken(String apiToken);
	boolean existsByEmail(String email);
	boolean existsByUsername(String username);
}

