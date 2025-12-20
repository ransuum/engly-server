package com.engly.engly_server.service.schedule;

import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import module java.base;

@Service
@RequiredArgsConstructor
public class IncompleteGoogleUserService {
    private final UserService userService;

    @Scheduled(cron = "0 */25 0 * * *")
    public void deleteUsers() {
        final Instant expireBefore = Instant.now().minus(Duration.ofMinutes(15));
        final List<Users> incomplete = userService.findAllByRolesAndCreatedAtBefore("ROLE_GOOGLE", expireBefore);
        userService.deleteAll(incomplete);
    }
}
