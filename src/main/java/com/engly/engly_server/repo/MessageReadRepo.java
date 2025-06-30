package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.MessageRead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageReadRepo extends JpaRepository<MessageRead, String> {

    // Check if user has read a specific message
    boolean existsByMessageIdAndUserId(String messageId, String userId);

    // Get all users who read a specific message
    List<MessageRead> findByMessageId(String messageId);

    // Get all messages read by a specific user
    Page<MessageRead> findByUserId(String userId, Pageable pageable);

    // Get read status for multiple messages by a user
    List<MessageRead> findByMessageIdInAndUserId(List<String> messageIds, String userId);
}
