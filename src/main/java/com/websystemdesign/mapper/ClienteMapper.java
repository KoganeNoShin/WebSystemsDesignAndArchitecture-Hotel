package com.websystemdesign.mapper;

import com.websystemdesign.dto.ClienteDto;
import com.websystemdesign.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    @Mapping(source = "utente.id", target = "utenteId")
    ClienteDto toDto(Cliente cliente);

    @Mapping(source = "utenteId", target = "utente.id")
    Cliente toEntity(ClienteDto clienteDto);
}
