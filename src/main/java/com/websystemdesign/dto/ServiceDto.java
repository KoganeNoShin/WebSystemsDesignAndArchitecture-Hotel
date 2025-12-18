package com.websystemdesign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ServiceDto {
    private Long id;

    @NotBlank(message = "Il nome del servizio è obbligatorio")
    private String nome;

    @PositiveOrZero(message = "Il costo non può essere negativo")
    private float costo;
}
