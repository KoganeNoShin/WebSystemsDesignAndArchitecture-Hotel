package com.websystemdesign.mapper;

import com.websystemdesign.dto.SedeDto;
import com.websystemdesign.model.Sede;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SedeMapper {
    SedeMapper INSTANCE = Mappers.getMapper(SedeMapper.class);

    SedeDto toDto(Sede sede);
    Sede toEntity(SedeDto sedeDto);
}
