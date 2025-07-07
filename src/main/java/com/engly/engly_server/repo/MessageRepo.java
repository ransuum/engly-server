package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, String> {
    Page<Message> findAllByRoomId(String roomId, Pageable pageable);

    @Query(value = "SELECT m FROM Message m WHERE m.room.id = :roomId AND m.content LIKE '%' || :keyString || '%'  ")
    Page<Message> findAllMessagesByRoomIdContainingKeyString(String roomId, String keyString, Pageable pageable);

    @Query(value = """
        SELECT * FROM messages
        WHERE room_id = :roomId
        AND (is_deleted IS NULL OR is_deleted = false)
        ORDER BY created_at DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Message> findMessagesByRoomIdPaginated(
            @Param("roomId") String roomId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
        SELECT COUNT(*) FROM messages
        WHERE room_id = :roomId
        AND (is_deleted IS NULL OR is_deleted = false)
        """, nativeQuery = true)
    long countMessagesByRoomId(@Param("roomId") String roomId);
}
