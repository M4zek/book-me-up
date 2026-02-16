package com.m4zek.backend.repository;

import com.m4zek.backend.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository {

    Message save(Message message);


    @Query("""
        select m from message m
            WHERE m.room.id = :room_id
                ORDER BY m.createdDate DESC
    """)
    Page<Message> findAllByRoomId(
            @Param("room_id") int room_id, Pageable pageable);

}
