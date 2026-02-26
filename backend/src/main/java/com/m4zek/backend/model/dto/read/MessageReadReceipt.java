package com.m4zek.backend.model.dto.read;

import com.m4zek.backend.model.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageReadReceipt {

    private int message_id;
    private int room_id;
    private int reader_id;
    private MessageType type;
}
