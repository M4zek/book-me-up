package com.m4zek.backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String addressEmail;
    private String password;
    private Boolean isBlock;
    private Boolean isEnable;


    @OneToOne
    @JoinColumn(name = "userDataId")
    private UserData userData;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "preferredUser")
    private List<Reservation> preferredReservations = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL
    )
    private List<CompanyUserRole> companyUserRoles = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<RoomUser> rooms;

    public User() {}

    public User(String addressEmail, String password, UserData userData, Set<Role> roles) {
        this.addressEmail = addressEmail;
        this.password = password;
        this.userData = userData;
        this.roles = roles;
        this.isEnable = true;
        this.isBlock = true;
    }

    public int getId() {
        return id;
    }

    public String getAddressEmail() {
        return addressEmail;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getBlock() {
        return isBlock;
    }

    public Boolean getEnable() {
        return isEnable;
    }

    public UserData getUserData() {
        return userData;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public List<CompanyUserRole> getCompanyUserRoles() {
        return companyUserRoles;
    }


    /**
     * Compares this User with another object for equality.
     * @param o the object to compare with
     * @return true if both objects represent the same persisted User
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id != 0 && id == user.id;
    }

    /**
     * @return the hash code value for this entity
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
