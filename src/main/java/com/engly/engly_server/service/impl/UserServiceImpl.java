package com.engly.engly_server.service.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<UsersDto> allUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(UserMapper.INSTANCE::toUsersDto);
    }


}
