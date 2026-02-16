package com.m4zek.backend.model.dto.write;


import com.m4zek.backend.model.MessageType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ChatMessageRequest {

    private int room_id;
    private String content;
    private MessageType type;

}
