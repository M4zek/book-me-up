package com.m4zek.backend.model;

import jakarta.persistence.*;

@Entity(name = "message")
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Message(){}

    public Message(MessageType messageType, String content, Room room, User user) {
        this.messageType = messageType;
        this.content = content;
        this.room = room;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public Room getRoom() {
        return room;
    }

    public User getUser() {
        return user;
    }
}
