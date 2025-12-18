package com.websystemdesign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class MultimediaDto {
    private Long id;

    @NotBlank(message = "Il nome del contenuto multimediale è obbligatorio")
    private String nome;

    // L'immagine (byte[]) non viene solitamente trasferita in un DTO standard.
    // Verrà gestita con un endpoint specifico per l'upload/download.

    @PositiveOrZero(message = "Il costo non può essere negativo")
    private float costo;
}
