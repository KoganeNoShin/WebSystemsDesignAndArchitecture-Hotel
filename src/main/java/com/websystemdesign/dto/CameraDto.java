package com.websystemdesign.dto;

import com.websystemdesign.model.StatoCamera;
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

    private boolean luce;
    private boolean tapparelle;
    private float temperatura;
}
