package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Rooms;
import lombok.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@NullMarked
public interface RoomRepository extends JpaRepository<Rooms, String>, JpaSpecificationExecutor<@NonNull Rooms> {
    Page<Rooms> findByCategoryId(String categoryId, Pageable pageable);

    boolean existsByName(String name);
}
