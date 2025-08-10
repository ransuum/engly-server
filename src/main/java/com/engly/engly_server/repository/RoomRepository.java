package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoomRepository extends JpaRepository<Rooms, String>, JpaSpecificationExecutor<Rooms> {
    Page<Rooms> findByCategoryName(CategoryType categoryName, Pageable pageable);

    boolean existsByName(String name);
}
