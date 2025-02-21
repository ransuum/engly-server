package com.engly.engly_server.utils.mapper;

import com.engly.engly_server.models.dto.StatisticsDto;
import com.engly.engly_server.models.entity.Statistics;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StatisticMapper {
    StatisticMapper INSTANCE = Mappers.getMapper(StatisticMapper.class);

    StatisticsDto toDto(Statistics statistics);
}
