package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import module java.base;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT u.username FROM Users u WHERE u.email = :email")
    Optional<String> findUsernameByEmail(@Param("email") String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Modifying
    @Query(value = "DELETE FROM Users WHERE id IN :ids", nativeQuery = true)
    int deleteAllByIdIn(List<String> ids);

    @Modifying
    @Query(value = "DELETE FROM users WHERE roles = :roles AND created_at < :expireBefore", nativeQuery = true)
    void deleteAllByRolesAndCreatedAtBefore(String roles, Instant expireBefore);
}
