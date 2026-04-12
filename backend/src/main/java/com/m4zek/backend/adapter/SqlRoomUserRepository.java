package com.m4zek.backend.adapter;

import com.m4zek.backend.model.RoomUser;
import com.m4zek.backend.repository.RoomUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlRoomUserRepository extends RoomUserRepository , JpaRepository<RoomUser, Long> {
}
