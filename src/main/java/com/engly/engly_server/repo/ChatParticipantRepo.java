package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.ChatParticipants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatParticipantRepo extends JpaRepository<ChatParticipants, String> {

    @Query(value = """
            select * from chat_participants
            WHERE room_id = :roomId
            """, nativeQuery = true)
    Page<ChatParticipants> findAllByRoom_Id(@Param("roomId") String roomId, Pageable pageable);

    boolean existsByRoomIdAndUserId(String roomId, String userId);
}
