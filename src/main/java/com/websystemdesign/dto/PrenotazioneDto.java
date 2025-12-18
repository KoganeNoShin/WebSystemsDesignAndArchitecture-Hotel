package com.websystemdesign.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class PrenotazioneDto {
    private Long id;

    @NotNull(message = "Il cliente è obbligatorio")
    private Long clienteId;

    @NotNull(message = "La camera è obbligatoria")
    private Long cameraId;

    @NotNull(message = "La data di inizio è obbligatoria")
    @FutureOrPresent(message = "La data di inizio non può essere nel passato")
    private LocalDate dataInizio;

    @NotNull(message = "La data di fine è obbligatoria")
    private LocalDate dataFine;

    @PositiveOrZero(message = "Il costo non può essere negativo")
    private float costo;

    // Includiamo i DTO delle entità collegate per avere una risposta completa
    private Set<NotaDto> note;
    private Set<OspiteDto> ospiti;
    private Set<ServiceDto> services;
    private Set<MultimediaDto> multimedia;
}
