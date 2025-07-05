package com.engly.engly_server.repo;

import com.engly.engly_server.models.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<Users> findAllByRolesAndCreatedAtBefore(String roles, Instant expireBefore);

    @Modifying
    @Query("DELETE FROM Users u WHERE u.id IN :ids")
    int deleteAllByIdIn(List<String> ids);
}
