package com.websystemdesign.mapper;

import com.websystemdesign.dto.DipendenteDto;
import com.websystemdesign.model.Dipendente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DipendenteMapper {
    DipendenteMapper INSTANCE = Mappers.getMapper(DipendenteMapper.class);

    @Mapping(source = "sede.id", target = "sedeId")
    @Mapping(source = "utente.id", target = "utenteId")
    DipendenteDto toDto(Dipendente dipendente);

    @Mapping(source = "sedeId", target = "sede.id")
    @Mapping(source = "utenteId", target = "utente.id")
    Dipendente toEntity(DipendenteDto dipendenteDto);
}
