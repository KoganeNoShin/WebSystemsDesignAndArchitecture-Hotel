package com.websystemdesign.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookingDetailDto {
    private Long id;
    private String stato;
    
    private String cameraNumero;
    private String cameraTipologia;
    private String sedeNome;
    private String sedeLocation;
    
    private LocalDate checkin;
    private LocalDate checkout;
    
    private float costoCamera;
    private float costoServizi;
    private float costoMultimedia;
    private float costoTotale;
    
    private List<String> servizi;
    
    private List<OspiteDto> ospiti;
    
    @Data
    public static class OspiteDto {
        private String nome;
        private String cognome;
        private String cittadinanza;
        private String dataNascita;
    }
}
