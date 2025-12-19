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
    @Mapping(source = "utente.nome", target = "nome")
    @Mapping(source = "utente.cognome", target = "cognome")
    @Mapping(source = "utente.username", target = "username")
    @Mapping(source = "sede.nome", target = "nomeSede")
    DipendenteDto toDto(Dipendente dipendente);

    @Mapping(source = "sedeId", target = "sede.id")
    @Mapping(source = "utenteId", target = "utente.id")
    Dipendente toEntity(DipendenteDto dipendenteDto);
}
