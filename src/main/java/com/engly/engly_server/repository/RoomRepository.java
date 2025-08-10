package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Rooms, String>, JpaSpecificationExecutor<Rooms> {
    Page<Rooms> findByCategoryName(CategoryType categoryName, Pageable pageable);

    @Query("SELECT r FROM Rooms r WHERE r.category.name = :categoryName AND " +
            "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Rooms> searchRooms(@Param("categoryName") CategoryType categoryName,
                            @Param("keyword") String keyword,
                            Pageable pageable);

    boolean existsByName(String name);
}
