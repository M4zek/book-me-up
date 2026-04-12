package com.m4zek.backend.adapter;

import com.m4zek.backend.model.Room;
import com.m4zek.backend.repository.RoomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlRoomRepository extends RoomRepository, JpaRepository<Room, Long> {
}
