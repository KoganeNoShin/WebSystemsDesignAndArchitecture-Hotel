package com.websystemdesign.dto;

import com.websystemdesign.model.StatoCamera;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CameraDto {
    private Long id;

    @NotNull(message = "La sede è obbligatoria")
    private Long sedeId;

    @Positive(message = "I posti letto devono essere almeno 1")
    private int postiLetto;

    @NotNull(message = "Lo stato della camera è obbligatorio")
    private StatoCamera status;

    @NotBlank(message = "Il numero o nome della camera è obbligatorio")
    private String numero; // Es. "101" o "Suite Vista Mare"

    @Positive(message = "Il prezzo base deve essere positivo")
    private float prezzoBase;

    private boolean luce;
    private boolean tapparelle;
    private float temperatura;
}
