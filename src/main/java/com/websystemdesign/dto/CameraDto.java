package com.websystemdesign.dto;

import com.websystemdesign.model.StatoCamera;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.List;

@Data
public class CameraDto {
    private Long id;

    @NotNull(message = "La sede è obbligatoria")
    private Long sedeId;

    @NotNull(message = "La tipologia è obbligatoria")
    private String tipologia = "Standard";

    @NotNull(message = "Il numero o nome della camera è obbligatorio")
    private String numero;

    @Positive(message = "I posti letto devono essere almeno 1")
    private int postiLetto;

    @NotNull(message = "Il prezzo è obbligatorio")
    @Positive(message = "Il prezzo base deve essere positivo")
    private Float prezzoBase;

    @NotNull(message = "Lo stato della camera è obbligatorio")
    private StatoCamera status;

    private boolean luce;
    private boolean tapparelle;
    private float temperatura;

    private List<String> immagini;
}
