package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepo extends JpaRepository<Rooms, String> {

    @Query(value = """
        SELECT r.* FROM rooms r
        JOIN categories c ON r.category_id = c.id
        WHERE c.name = :#{#name.name()}
        """,nativeQuery = true)
    Page<Rooms> findAllByCategoryName(@Param("name") CategoryType name, Pageable pageable);

    @Query("""
            SELECT r FROM Rooms r
            WHERE r.category.name = :categoryName
            AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyString, '%'))
                 OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyString, '%')))
            """)
    Page<Rooms> findRoomsByCategoryAndKeyword(@Param("keyString") String keyString,
                                              @Param("categoryName") CategoryType categoryName,
                                              Pageable pageable);
}
