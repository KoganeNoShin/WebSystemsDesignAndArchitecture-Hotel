package com.websystemdesign.mapper;

import com.websystemdesign.dto.MultimediaDto;
import com.websystemdesign.model.Multimedia;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MultimediaMapper {
    MultimediaMapper INSTANCE = Mappers.getMapper(MultimediaMapper.class);

    MultimediaDto toDto(Multimedia multimedia);
    Multimedia toEntity(MultimediaDto multimediaDto);
}
