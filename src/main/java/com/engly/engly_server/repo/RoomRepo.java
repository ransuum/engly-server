package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepo extends JpaRepository<Rooms, String> {
}
