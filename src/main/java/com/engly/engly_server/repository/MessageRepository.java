package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, String>, JpaSpecificationExecutor<Message> {

    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId AND m.content LIKE %:content% AND m.isDeleted = false")
    Page<Message> search(@Param("roomId") String roomId, @Param("content") String content, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId AND m.isDeleted = false ORDER BY m.createdAt ASC")
    Page<Message> findActive(@Param("roomId") String roomId, Pageable pageable);
}
