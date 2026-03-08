package com.idexx.vetsoftware.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vetroles")
public class Role {

    public enum ERole {
        ROLE_USER,
        ROLE_ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name; // use enum instead of String

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}