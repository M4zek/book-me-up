package com.m4zek.backend.repository;

import com.m4zek.backend.model.RoomUser;

import java.util.Optional;

public interface RoomUserRepository {

    boolean existsByRoomIdAndUserId(int room_id, int userId);

    Optional<RoomUser> findByRoomIdAndUserId(int room_id, int userId);

    RoomUser save(RoomUser roomUser);

}
