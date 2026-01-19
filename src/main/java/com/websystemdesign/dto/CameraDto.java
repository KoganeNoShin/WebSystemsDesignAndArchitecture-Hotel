package com.websystemdesign.dto;

import com.websystemdesign.model.StatoCamera;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CameraDto {
    private Long id;

    @NotNull(message = "La sede è obbligatoria")
    private Long sedeId;

    @NotNull(message = "La tipologia è obbligatoria")
    private String tipologia = "Standard";

    @NotNull(message = "Il numero della camera è obbligatorio")
    @Pattern(regexp = "^\\d{3}$", message = "Il campo deve contenere esattamente 3 cifre")
    private String numero;

    @Min(value = 1, message = "I posti letto devono essere almeno 1")
    @Max(value = 8, message = "I posti letto non possono essere più di 8")
    private int postiLetto;

    @NotNull(message = "Il prezzo è obbligatorio")
    @Min(value = 100, message = "Il prezzo base deve essere almeno 100€")
    private Float prezzoBase;

    @NotNull(message = "Lo stato della camera è obbligatorio")
    private StatoCamera status;

    private boolean luce;
    private boolean tapparelle;

    @Min(value = 16, message = "La temperatura minima è 16°C")
    @Max(value = 24, message = "La temperatura massima è 24°C")
    private float temperatura;

    private List<String> immagini;
}
