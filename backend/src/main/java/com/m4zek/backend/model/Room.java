package com.m4zek.backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "room")
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = true, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoomType type = RoomType.PRIVATE;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RoomUser> roles = new ArrayList<>();

    public Room() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RoomType getType() {
        return type;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<RoomUser> getRoles() {
        return roles;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }
}
