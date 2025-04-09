package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepo extends JpaRepository<Message, String> {
    Page<Message> findAllByRoomId(String roomId, Pageable pageable);
}
