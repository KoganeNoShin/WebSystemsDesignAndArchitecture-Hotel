package com.websystemdesign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotaDto {
    private Long prenotazioneId;
    private String testo;
    private String data;
    
    public NotaDto(String testo, String data) {
        this.testo = testo;
        this.data = data;
    }
}
