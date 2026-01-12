package com.websystemdesign.mapper;

import com.websystemdesign.dto.UtenteDto;
import com.websystemdesign.model.Utente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

    UtenteMapper INSTANCE = Mappers.getMapper(UtenteMapper.class);

    UtenteDto toDto(Utente utente);

    @Mapping(target = "password", ignore = true)
    Utente toEntity(UtenteDto utenteDto);
}
