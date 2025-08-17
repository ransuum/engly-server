package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.ChatParticipants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipants, String> {

    Page<ChatParticipants> findAllByRoomId(@Param("roomId") String roomId, Pageable pageable);

    boolean existsByRoomIdAndUserId(String roomId, String userId);

    @Query("SELECT cp FROM ChatParticipants cp WHERE cp.user.email = :email AND cp.leaveAt IS NULL")
    List<ChatParticipants> findByEmail(@Param("email") String email);

    @Query("SELECT cp FROM ChatParticipants cp WHERE cp.user.email = :email AND cp.room.id = :roomId AND cp.leaveAt IS NULL")
    Optional<ChatParticipants> findByEmailAndRoom(@Param("email") String email, @Param("roomId") String roomId);
}
