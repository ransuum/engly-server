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
        """, nativeQuery = true)
    Page<Rooms> findAllByCategoryName(@Param("name") CategoryType name, Pageable pageable);

    @Query(value = """
            SELECT r FROM Rooms r WHERE
            LOWER(r.category.name) LIKE LOWER('%' || :keyString || '%') OR
            LOWER(r.name) LIKE LOWER('%' || :keyString || '%') OR
            LOWER(r.description) LIKE LOWER('%' || :keyString || '%')
            """
    )
    Page<Rooms> findAllRoomsContainingKeyString(String keyString, Pageable pageable);

    Page<Rooms> findAllByNameContainingIgnoreCaseAndCategoryName(String keyString, CategoryType categoryName, Pageable pageable);
}
