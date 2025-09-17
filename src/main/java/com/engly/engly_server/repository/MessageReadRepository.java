package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.MessageRead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MessageReadRepository extends JpaRepository<MessageRead, String> {

    boolean existsByMessageIdAndUserId(String messageId, String userId);

    Page<MessageRead> findAllByMessageId(@Param("messageIds") String messageId, Pageable pageable);
}
