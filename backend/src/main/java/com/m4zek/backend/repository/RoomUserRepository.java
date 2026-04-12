package com.m4zek.backend.repository;

import com.m4zek.backend.model.RoomUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomUserRepository {

    boolean existsByRoomIdAndUserId(int room_id, int userId);

    Optional<RoomUser> findByRoomIdAndUserId(int room_id, int userId);

    RoomUser save(RoomUser roomUser);

    @Query("""
        SELECT ru
        FROM room_users ru
        WHERE ru.user.id = :userA
          AND ru.room.type = com.m4zek.backend.model.RoomType.PRIVATE
          AND ru.room.id IN (
                SELECT ru2.room.id
                FROM room_users ru2
                WHERE ru2.user.id IN (:userA, :userB)
                GROUP BY ru2.room.id
                HAVING COUNT(DISTINCT ru2.user.id) = 2
          )
          AND (
                SELECT COUNT(ru3)
                FROM room_users ru3
                WHERE ru3.room.id = ru.room.id
          ) = 2
    """)
    Optional<RoomUser> findPrivateConversation(
            @Param("userA") int userA,
            @Param("userB") int userB
    );
}
