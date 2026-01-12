package com.websystemdesign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class MultimediaDto {
    private Long id;

    @NotBlank(message = "Il nome del contenuto multimediale è obbligatorio")
    private String nome;

    @PositiveOrZero(message = "Il costo non può essere negativo")
    private float costo;
}
