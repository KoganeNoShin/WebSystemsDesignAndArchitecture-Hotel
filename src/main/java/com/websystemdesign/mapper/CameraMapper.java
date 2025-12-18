package com.websystemdesign.mapper;

import com.websystemdesign.dto.CameraDto;
import com.websystemdesign.model.Camera;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CameraMapper {
    CameraMapper INSTANCE = Mappers.getMapper(CameraMapper.class);

    @Mapping(source = "sede.id", target = "sedeId")
    CameraDto toDto(Camera camera);

    @Mapping(source = "sedeId", target = "sede.id")
    Camera toEntity(CameraDto cameraDto);
}
