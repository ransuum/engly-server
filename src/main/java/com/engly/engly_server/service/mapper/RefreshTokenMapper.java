package com.engly.engly_server.service.mapper;

import com.engly.engly_server.models.dto.response.RefreshTokenDto;
import com.engly.engly_server.models.entity.RefreshToken;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    @Mapping(target = "token", qualifiedByName = "normalizeToken")
    @NonNull RefreshTokenDto toDisplayDto(@NonNull RefreshToken source);

    @Named("normalizeToken")
    default @NonNull String normalizeToken(@NonNull String token) {
        if (token.length() <= 12) return token;
        return token.substring(0, 8) + "..." +
                token.substring(token.length() - 4);
    }
}
