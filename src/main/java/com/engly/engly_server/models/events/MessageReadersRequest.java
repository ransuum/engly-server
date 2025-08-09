package com.engly.engly_server.models.events;

import org.springframework.data.domain.Pageable;

public record MessageReadersRequest(String roomId,
                                    String messageId,
                                    Pageable pageable) { }
