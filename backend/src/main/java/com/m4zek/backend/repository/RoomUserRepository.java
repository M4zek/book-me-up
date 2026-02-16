package com.m4zek.backend.repository;

public interface RoomUserRepository {

    boolean existsByRoomIdAndUserId(int room_id, int userId);

}
