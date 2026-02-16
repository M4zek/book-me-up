package com.m4zek.backend.model.dto.read;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.m4zek.backend.model.MessageType;
import com.m4zek.backend.model.projection.MemberProjection;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class ChatMessageResponse {

    private Long id;
    private String content;
    private MemberProjection sender;
    private MessageType type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private ZonedDateTime createdDate;
}

