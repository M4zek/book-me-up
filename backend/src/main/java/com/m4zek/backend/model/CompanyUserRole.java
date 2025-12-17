package com.m4zek.backend.model;

import jakarta.persistence.*;

@Entity(name = "company_user_roles")
public class CompanyUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_role_id", nullable = false)
    private CompanyRole role;

    public CompanyUserRole() {}

    public CompanyUserRole(User user, Company company, CompanyRole role) {
        this.user = user;
        this.company = company;
        this.role = role;
    }

    public CompanyRole getRole() {
        return this.role;
    }

    public User getUser() {
        return this.user;
    }

    public Company getCompany() {return this.company;}
}

