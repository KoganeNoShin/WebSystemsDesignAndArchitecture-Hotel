package com.websystemdesign.mapper;

import com.websystemdesign.dto.NotaDto;
import com.websystemdesign.model.Nota;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotaMapper {
    NotaMapper INSTANCE = Mappers.getMapper(NotaMapper.class);

    @Mapping(source = "prenotazione.id", target = "prenotazioneId")
    NotaDto toDto(Nota nota);

    @Mapping(source = "prenotazioneId", target = "prenotazione.id")
    Nota toEntity(NotaDto notaDto);
}
