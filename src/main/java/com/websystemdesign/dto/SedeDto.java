package com.websystemdesign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SedeDto {
    private Long id;

    @NotBlank(message = "Il nome della sede è obbligatorio")
    private String nome;

    @NotBlank(message = "La location è obbligatoria")
    private String location;

    @NotBlank(message = "La tassa di soggiorno è obbligatoria")
    @Pattern(regexp = "^[0-9]+\\.[0-9]{2}$", message = "Formato non valido. Usa il formato XX.XX")
    private String tassaSoggiorno;
}
