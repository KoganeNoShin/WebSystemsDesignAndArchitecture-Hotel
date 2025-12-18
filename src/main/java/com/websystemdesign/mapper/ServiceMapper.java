package com.websystemdesign.mapper;

import com.websystemdesign.dto.ServiceDto;
import com.websystemdesign.model.Service;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    ServiceDto toDto(Service service);
    Service toEntity(ServiceDto serviceDto);
}
