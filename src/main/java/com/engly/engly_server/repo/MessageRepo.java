package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepo extends JpaRepository<Message, String> {
    Page<Message> findAllByRoomId(String roomId, Pageable pageable);

    @Query(value = "SELECT m FROM Message m WHERE m.room.id = :roomId AND m.content LIKE '%' || :keyString || '%'  ")
    Page<Message> findAllMessagesByRoomIdContainingKeyString(String roomId, String keyString, Pageable pageable);
}
