package com.engly.engly_server.repository;

import com.engly.engly_server.models.dto.response.RoomProjection;
import com.engly.engly_server.models.entity.Rooms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Rooms, String>, JpaSpecificationExecutor<Rooms> {
    Page<Rooms> findByCategoryId(String categoryId, Pageable pageable);

    @Query(
            value = "SELECT * FROM get_rooms_with_last_message(:categoryId)",
            countQuery = "SELECT count(*) FROM rooms WHERE :categoryId IS NULL OR category_id = :categoryId",
            nativeQuery = true
    )
    Page<RoomProjection> findRoomsWithLastMessage(@Param("categoryId") String categoryId, Pageable pageable);


    boolean existsByName(String name);
}
