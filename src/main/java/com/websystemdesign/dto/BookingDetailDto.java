package com.websystemdesign.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookingDetailDto {
    private Long id;
    private String stato;
    
    // Camera
    private String cameraNumero;
    private String cameraTipologia;
    private String sedeNome;
    private String sedeLocation;
    
    // Date
    private LocalDate checkin;
    private LocalDate checkout;
    
    // Costi
    private float costoTotale;
    
    // Servizi
    private List<String> servizi;
    
    // Ospiti (se presenti)
    private List<OspiteDto> ospiti;
    
    @Data
    public static class OspiteDto {
        private String nome;
        private String cognome;
        private String cittadinanza;
        private String dataNascita;
    }
}
