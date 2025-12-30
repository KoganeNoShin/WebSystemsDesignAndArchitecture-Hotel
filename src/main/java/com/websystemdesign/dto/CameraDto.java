package com.websystemdesign.dto;

import lombok.Data;
import java.util.List;

@Data
public class CameraDto {
    private Long id;
    private String numero;
    private String tipologia;
    private int postiLetto;
    private float prezzoBase;
    private List<String> immagini;
    private Long sedeId;
    
    // Campi domotica (aggiunti per compatibilità mapping)
    private boolean luce;
    private boolean tapparelle;
    private float temperatura;
}
