package com.websystemdesign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotaDto {
    private Long id;

    @NotNull(message = "L'ID della prenotazione è obbligatorio")
    private Long prenotazioneId;

    @NotBlank(message = "Il testo della nota è obbligatorio")
    private String testo;

    private String data;
    
    public NotaDto(String testo, String data) {
        this.testo = testo;
        this.data = data;
    }
}
