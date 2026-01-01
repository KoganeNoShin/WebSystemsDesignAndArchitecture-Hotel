package com.websystemdesign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotaDto {
    private Long prenotazioneId; // Aggiunto per risolvere errore di mapping
    private String testo;
    private String data;
    
    // Costruttore per compatibilità con il controller esistente
    public NotaDto(String testo, String data) {
        this.testo = testo;
        this.data = data;
    }
}
