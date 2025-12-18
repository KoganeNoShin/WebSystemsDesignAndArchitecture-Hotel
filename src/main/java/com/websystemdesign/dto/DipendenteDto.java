package com.websystemdesign.dto;

import com.websystemdesign.model.Ruolo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DipendenteDto {
    private Long id;

    @NotNull(message = "Il ruolo è obbligatorio")
    private Ruolo ruolo;

    private Long sedeId;
    private Long utenteId;
}
