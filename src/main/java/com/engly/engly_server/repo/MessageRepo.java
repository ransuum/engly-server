package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Messages, String> {
}
