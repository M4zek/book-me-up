package com.m4zek.backend.adapter;

import com.m4zek.backend.model.Message;
import com.m4zek.backend.repository.ChatMessageRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlChatMessageRepository extends ChatMessageRepository, JpaRepository<Message, Long> {
}
