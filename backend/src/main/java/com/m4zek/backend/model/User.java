package com.m4zek.backend.model;

import com.m4zek.backend.model.projection.UserReadModel;
import com.m4zek.backend.security.service.MyUserDetails;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    public User() {}

    public User(String addressEmail, String password, UserData userData, Set<Role> roles) {
        this.addressEmail = addressEmail;
        this.password = password;
        this.userData = userData;
        this.roles = roles;
        this.isEnable = true;
        this.isBlock = true;
    }



    public UserReadModel toUserReadModel() {
        return userData.toUserReadModel(this.id, this.addressEmail);
    }


    public MyUserDetails toMyUserDetails() {
        List<GrantedAuthority> authorities = this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new MyUserDetails(
                this.id,
                this.addressEmail,
                this.password,
                this.isBlock,
                this.isEnable,
                this.userData.toUserReadModel().getId(),
                authorities
        );
    }


}
