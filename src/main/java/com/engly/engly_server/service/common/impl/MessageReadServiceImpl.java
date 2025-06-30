package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.mapper.UserMapper;
import com.engly.engly_server.models.dto.UserWhoReadsMessageDto;
import com.engly.engly_server.models.entity.Message;
import com.engly.engly_server.models.entity.MessageRead;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.repo.MessageReadRepo;
import com.engly.engly_server.service.common.MessageReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageReadServiceImpl implements MessageReadService {

    private MessageReadRepo messageReadRepository;

    @Override
    public void markMessageAsRead(String messageId, String userId) {
        if (!messageReadRepository.existsByMessageIdAndUserId(messageId, userId)) {
            MessageRead messageRead = MessageRead.builder()
                    .message(Message.builder().id(messageId).build())
                    .user(Users.builder().id(userId).build())
                    .build();
            messageReadRepository.save(messageRead);
        }
    }

    @Override
    public List<UserWhoReadsMessageDto> getUsersWhoReadMessage(String messageId) {
        return messageReadRepository.findByMessageId(messageId)
                .stream()
                .map(mr -> UserMapper.INSTANCE.toWhoReadMessage(mr.getUser()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasUserReadMessage(String messageId, String userId) {
        return messageReadRepository.existsByMessageIdAndUserId(messageId, userId);
    }
}