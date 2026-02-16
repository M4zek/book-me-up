package com.m4zek.backend.model;

import jakarta.persistence.*;

@Entity(name = "room_users")
public class RoomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoomRole role = RoomRole.MEMBER;

    public RoomUser(Room room, User user, RoomRole role) {
        this.room = room;
        this.user = user;
        this.role = role;
    }

    public RoomUser() {

    }

    public Room getRoom() {
        return room;
    }

    public User getUser() {
        return user;
    }

    public RoomRole getRole() {
        return role;
    }
}
