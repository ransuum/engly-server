package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Message;
import lombok.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@NullMarked
public interface MessageRepository extends JpaRepository<Message, String>, JpaSpecificationExecutor<@NonNull Message> {
    @Query("SELECT m FROM Message m WHERE m.roomId = :roomId AND m.isDeleted = false")
    Page<Message> findActive(@Param("roomId") String roomId, Pageable pageable);
}
