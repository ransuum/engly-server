package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.ChatParticipants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ChatParticipantRepo extends JpaRepository<ChatParticipants, String> {

    Page<ChatParticipants> findAllByRoomId(@Param("roomId") String roomId, Pageable pageable);

    boolean existsByRoomIdAndUserId(String roomId, String userId);
}
