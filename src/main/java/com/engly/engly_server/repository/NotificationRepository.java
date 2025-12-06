package com.engly.engly_server.repository;

import com.engly.engly_server.models.entity.Notifications;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

@NullMarked
public interface NotificationRepository extends JpaRepository<Notifications, String> {
}
