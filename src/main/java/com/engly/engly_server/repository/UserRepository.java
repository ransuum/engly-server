package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT u.username FROM Users u WHERE u.email = :email")
    Optional<String> findUsernameByEmail(@Param("email") String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<Users> findAllByRolesAndCreatedAtBefore(String roles, Instant expireBefore);

    @Modifying
    @Query("DELETE FROM Users u WHERE u.id IN :ids")
    int deleteAllByIdIn(List<String> ids);
}
