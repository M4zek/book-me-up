package com.m4zek.backend.model;

import com.m4zek.backend.model.projection.UserReadModel;
import jakarta.persistence.*;

@Entity(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String addressEmail;
    private String password;

    @OneToOne
    @JoinColumn(name = "userDataId")
    private UserData userData;


    public User() {}

    public User(String addressEmail, String password, UserData userData) {
        this.addressEmail = addressEmail;
        this.password = password;
        this.userData = userData;
    }

    public UserReadModel toUserReadModel() {
        return userData.toUserReadModel(this.id, this.addressEmail);
    }

}
