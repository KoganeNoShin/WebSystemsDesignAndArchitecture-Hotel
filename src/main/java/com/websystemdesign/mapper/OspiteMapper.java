package com.websystemdesign.mapper;

import com.websystemdesign.dto.OspiteDto;
import com.websystemdesign.model.Ospite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OspiteMapper {
    OspiteMapper INSTANCE = Mappers.getMapper(OspiteMapper.class);

    @Mapping(source = "prenotazione.id", target = "prenotazioneId")
    OspiteDto toDto(Ospite ospite);

    @Mapping(source = "prenotazioneId", target = "prenotazione.id")
    Ospite toEntity(OspiteDto ospiteDto);
}
