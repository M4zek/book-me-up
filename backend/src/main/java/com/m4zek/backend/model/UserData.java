package com.m4zek.backend.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity(name = "users_data")
public class UserData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String phoneNumber;
    @Lob
    private byte[] photo;

    @OneToOne(mappedBy = "userData", cascade = CascadeType.ALL)
    private User user;


    public UserData() {}

    public UserData(String firstName, String lastName, Date dateOfBirth, String phoneNumber, byte[] photo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
    }


    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public byte[] getPhoto() {
        return photo;
    }
}
