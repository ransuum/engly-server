package com.engly.engly_server.config.websocket.models;

import org.springframework.data.domain.Pageable;

public record MessageReadersRequest(String messageId,
                                    String roomId,
                                    Pageable pageable) { }
