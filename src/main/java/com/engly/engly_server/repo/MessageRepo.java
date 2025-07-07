package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepo extends JpaRepository<Message, String> {

    @Query(value = """
            SELECT * FROM messages
            WHERE room_id = :roomId
            AND content LIKE '%' || :keyString || '%'
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    Page<Message> findAllMessagesByRoomIdContainingKeyString(@Param("roomId") String roomId,
                                                             Pageable pageable,
                                                             @Param("keyString") String keyString);

    @Query(value = """
            SELECT * FROM messages
            WHERE room_id = :roomId
            AND (is_deleted IS NULL OR is_deleted = false)
            ORDER BY created_at
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    Page<Message> findMessagesByRoomIdPaginated(
            @Param("roomId") String roomId, Pageable pageable);
}
