package com.idexx.vetsoftware.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.idexx.vetsoftware.dto.ApiResponse;
import com.idexx.vetsoftware.dto.LoginRequest;
import com.idexx.vetsoftware.dto.UserInfoResponse;
import com.idexx.vetsoftware.model.Role;
import com.idexx.vetsoftware.model.User;
import com.idexx.vetsoftware.repository.RoleRepository;
import com.idexx.vetsoftware.repository.UserRepository;
import com.idexx.vetsoftware.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/vetusers")
public class UserController {
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserService userService;

    // ✅ Admin only – view all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id, Principal principal) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();

        // Optional: Allow only admins or the user themselves
        if (!principal.getName().equals(user.getUsername()) && 
            !SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        return ResponseEntity.ok(user);
    }


    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser, Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());

        if (!optionalUser.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User existingUser = optionalUser.get(); // ✅ extract the actual User object

        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUsername(updatedUser.getUsername());

        userRepository.save(existingUser); // ✅ save the actual entity

        return ResponseEntity.ok("Profile updated successfully!");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userRepository.delete(optionalUser.get());
        return ResponseEntity.ok("User deleted successfully!");
    }

    @PostMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRole(@PathVariable Long id, @RequestParam String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role name"));

        user.getRoles().add(role);
        userRepository.save(user);

        return ResponseEntity.ok("Role assigned successfully");
    }

    @DeleteMapping("/{id}/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeRole(@PathVariable Long id, @PathVariable String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role name"));

        user.getRoles().remove(role);
        userRepository.save(user);

        return ResponseEntity.ok("Role removed successfully");
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());

        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();

        // Optionally, return only specific fields (DTO recommended in production)
        return ResponseEntity.ok().body(new UserInfoResponse("USER_RETRIEVED", user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail()));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserInfoResponse registrationRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use!");
        }
        
        log.info("From UserController Register request: {}", registrationRequest.getFirstName());
        
        // Create new user
        User user = new User();
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword())); // Make sure to inject passwordEncoder
        
        // Assign default role (USER)
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Default USER role not found"));
        if (!user.getRoles().contains(userRole)) {
            user.getRoles().add(userRole);
        }
        
        userRepository.save(user);
        
        return ResponseEntity.ok(new ApiResponse("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            String token = userService.authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            return ResponseEntity.ok().body(new ApiResponse(token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Invalid username or password"));
        }
    }
}