// src/main/java/com/idexx/vetsoftware/service/UserService.java
package com.idexx.vetsoftware.service;

import com.idexx.vetsoftware.dto.UserInfoResponse;
import com.idexx.vetsoftware.kafka.producer.EventProducer;
import com.idexx.vetsoftware.model.Role;
import com.idexx.vetsoftware.model.User;
import com.idexx.vetsoftware.repository.RoleRepository;
import com.idexx.vetsoftware.repository.UserRepository;
import com.idexx.vetsoftware.security.JwtService;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EventProducer eventProducer;

    @Autowired
    public UserService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        @Lazy AuthenticationManager authenticationManager,
        JwtService jwtService,
        @Autowired(required = false) EventProducer eventProducer
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.eventProducer = eventProducer;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    	User user = userRepository.findByUsername(username)
    	        .orElseGet(() -> userRepository.findByEmail(username));

    	if (user == null) {
    	    throw new UsernameNotFoundException("User not found: " + username);
    	}

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );
    }
    
    public User saveUser(User registrationRequest) {
        // Check if user already exists
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }
        
        log.info("From UserService Register request: {}", registrationRequest);

        // Create new user
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        // Assign default role
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Default role not found."));
        user.setRoles(Collections.singleton(userRole));

        User savedUser = userRepository.save(user);
        log.info("Successfully created new user with username: {}", savedUser.getUsername());

        // Send event to Kafka
        UserInfoResponse event = new UserInfoResponse(
            "USER_CREATED",
            savedUser.getId(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getUsername(),
            savedUser.getEmail()
        );
        if(eventProducer != null){
        	 eventProducer.sendUserEvent(event);
        }

        return savedUser;
    }

    public String authenticateUser(String username, String password) {
    	Authentication authentication = authenticationManager.authenticate(
    	        new UsernamePasswordAuthenticationToken(username, password)
    	);

    	SecurityContextHolder.getContext().setAuthentication(authentication);
    	log.info("User '{}' authenticated successfully.", username);

    	// Extract username and authorities
    	String jwtUsername = authentication.getName();
    	Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    	// Generate token using the existing JwtService method
    	return jwtService.generateToken(jwtUsername, authorities);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        userRepository.deleteById(user.getId());
        log.info("User with ID '{}' deleted.", id);

        // Send event to Kafka
        UserInfoResponse event = new UserInfoResponse(
            "USER_DELETED",
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getUsername(),
            user.getEmail()
        );
        if(eventProducer != null){
        	eventProducer.sendUserEvent(event);
        }
        
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
            .map(user -> {
                // Update fields if they are not null in the request
                if (userDetails.getEmail() != null) {
                    user.setEmail(userDetails.getEmail());
                }
                if (userDetails.getFirstName() != null) {
                    user.setFirstName(userDetails.getFirstName());
                }
                if (userDetails.getLastName() != null) {
                    user.setLastName(userDetails.getLastName());
                }

                User updatedUser = userRepository.save(user);
                log.info("User with ID '{}' updated.", id);

                // Send event to Kafka
                UserInfoResponse event = new UserInfoResponse(
                    "USER_UPDATED",
                    updatedUser.getId(),
                    updatedUser.getFirstName(),
                    updatedUser.getLastName(),
                    updatedUser.getUsername(),
                    updatedUser.getEmail()
                );
                if(eventProducer != null){
                	eventProducer.sendUserEvent(event);
                }

                return updatedUser;
            })
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}