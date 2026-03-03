package com.m4zek.backend.repository;

import com.m4zek.backend.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository {

    Optional<Room> findById(int id);

    Room save(Room room);

    @Query("""
        select r from room r
            left join r.roles s
            where s.user.id = :user_id
            order by (
                select max(m.createdDate)
                from message m
                where m.room = r
            ) DESC
    """)
    Page<Room> findAllByUserIdAndSortByLastMessage(@Param("user_id") int user_id, Pageable pageable);

}
