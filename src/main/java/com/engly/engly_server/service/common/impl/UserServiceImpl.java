package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.models.dto.ApiResponse;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.service.common.UserService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.USER_ID, key = "#id"),
            @CacheEvict(value = CacheName.ALL_USER, allEntries = true)
    })
    public ApiResponse delete(String id) {
        return userRepo.findById(id)
                .map(users -> {
                    this.clearUserSpecificCaches(users.getEmail(), users.getUsername());
                    userRepo.delete(users);
                    return new ApiResponse("User deleted successfully");
                })
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.USER_ID, key = "#id", sync = true)
    public UsersDto findById(String id) {
        return UserMapper.INSTANCE.toUsersDto(userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    @Cacheable(value = CacheName.USER_ENTITY_BY_EMAIL, key = "#email", sync = true)
    public Optional<Users> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    @Cacheable(value = CacheName.USER_BY_EMAIL_DTO, key = "#email", sync = true)
    public UsersDto findByEmailDto(String email) {
        return userRepo.findByEmail(email).map(UserMapper.INSTANCE::toUsersDto)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Caching(evict = {
            @CacheEvict(value = CacheName.USER_BY_EMAIL, key = "#email.toLowerCase()"),
            @CacheEvict(value = CacheName.USER_ID_BY_EMAIL, key = "#email.toLowerCase()"),
            @CacheEvict(value = CacheName.USER_BY_EMAIL_DTO, key = "#email.toLowerCase()"),
            @CacheEvict(value = CacheName.USER_ENTITY_BY_EMAIL, key = "#email.toLowerCase()"),
            @CacheEvict(value = CacheName.USERNAME_BY_EMAIL, key = "#email.toLowerCase()"),
            @CacheEvict(value = CacheName.USER_PROFILES, key = "#username"),
            @CacheEvict(value = CacheName.USERNAME_AVAILABILITY, key = "#username.toLowerCase()"),
            @CacheEvict(value = CacheName.EMAIL_AVAILABILITY, key = "#email.toLowerCase()"),
            @CacheEvict(value = CacheName.USER_FIRST_LOGIN, allEntries = true)
    })
    public void clearUserSpecificCaches(String email, String username) {
        log.debug("Cleared user-specific caches for email: {}, username: {}", email, username);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.ALL_USER,
            key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize",
            condition = "#pageable.pageNumber < 3 && #pageable.pageSize <= 20"
    )
    public Page<UsersDto> allUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(UserMapper.INSTANCE::toUsersDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.USERNAME_BY_EMAIL, key = "#email.toLowerCase()", sync = true)
    public String getUsernameByEmail(String email) {
        return userRepo.findUsernameByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.USER_ID, allEntries = true),
            @CacheEvict(value = CacheName.USER_BY_EMAIL, allEntries = true),
            @CacheEvict(value = CacheName.USER_ID_BY_EMAIL, allEntries = true),
            @CacheEvict(value = CacheName.USERNAME_BY_EMAIL, allEntries = true),
            @CacheEvict(value = CacheName.USER_PROFILES, allEntries = true),
            @CacheEvict(value = CacheName.ALL_USER, allEntries = true),
            @CacheEvict(value = CacheName.USERNAME_AVAILABILITY, allEntries = true),
            @CacheEvict(value = CacheName.USER_ENTITY_BY_EMAIL, allEntries = true),
            @CacheEvict(value = CacheName.EMAIL_AVAILABILITY, allEntries = true)
    })
    public Integer deleteSomeUsers(List<String> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return userRepo.deleteAllByIdIn(ids);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.USER_BY_EMAIL, key = "#email.toLowerCase()", sync = true)
    public Users findUserEntityByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    @Cacheable(value = CacheName.USER_ID_BY_EMAIL, key = "#email.toLowerCase()", sync = true)
    public String getUserIdByEmail(String email) {
        return userRepo.findByEmail(email).map(Users::getId)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    public List<Users> findAllByRolesAndCreatedAtBefore(String roles, Instant expireBefore) {
        return userRepo.findAllByRolesAndCreatedAtBefore(roles, expireBefore);
    }

    @Override
    @Transactional
    public void deleteAll(List<Users> users) {
        if (users != null) userRepo.deleteAll(users);
    }
}
