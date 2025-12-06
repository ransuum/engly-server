package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Moderation;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

@NullMarked
public interface ModerationRepository extends JpaRepository<Moderation, String> {
}
