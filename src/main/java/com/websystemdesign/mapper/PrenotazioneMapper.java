package com.websystemdesign.mapper;

import com.websystemdesign.dto.PrenotazioneDto;
import com.websystemdesign.model.Prenotazione;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {NotaMapper.class, OspiteMapper.class, ServiceMapper.class, MultimediaMapper.class})
public interface PrenotazioneMapper {
    PrenotazioneMapper INSTANCE = Mappers.getMapper(PrenotazioneMapper.class);

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "camera.id", target = "cameraId")
    PrenotazioneDto toDto(Prenotazione prenotazione);

    @Mapping(source = "clienteId", target = "cliente.id")
    @Mapping(source = "cameraId", target = "camera.id")
    Prenotazione toEntity(PrenotazioneDto prenotazioneDto);
}
