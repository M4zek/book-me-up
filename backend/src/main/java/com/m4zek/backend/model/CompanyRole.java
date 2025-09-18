package com.m4zek.backend.model;


import jakarta.persistence.*;

import java.util.Set;

@Entity(name = "company_roles")
public class CompanyRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompanyUserRole> users;


    public String getName() {
        return name;
    }
}
