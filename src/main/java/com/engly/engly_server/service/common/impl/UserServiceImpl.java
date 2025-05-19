package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.service.common.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Override
    @Transactional
    public void delete(String id) {
        final var user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userRepo.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UsersDto findById(String id) {
        return UserMapper.INSTANCE.toUsersDto(userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsersDto> allUsers() {
        return userRepo.findAll().stream()
                .map(UserMapper.INSTANCE::toUsersDto)
                .toList();
    }
}
