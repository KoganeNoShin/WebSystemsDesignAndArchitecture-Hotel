package com.websystemdesign.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotaDto {
    private Long id;

    @NotBlank(message = "La nota non può essere vuota")
    private String nota;

    private Long prenotazioneId;
}
