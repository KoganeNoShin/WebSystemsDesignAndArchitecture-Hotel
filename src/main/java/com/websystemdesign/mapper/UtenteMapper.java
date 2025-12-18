package com.websystemdesign.mapper;

import com.websystemdesign.dto.UtenteDto;
import com.websystemdesign.model.Utente;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // Dice a MapStruct di creare un bean Spring
public interface UtenteMapper {

    UtenteMapper INSTANCE = Mappers.getMapper(UtenteMapper.class);

    // Metodo per convertire da Entità a DTO
    UtenteDto toDto(Utente utente);

    // Metodo per convertire da DTO a Entità
    Utente toEntity(UtenteDto utenteDto);
}
