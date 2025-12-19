package com.websystemdesign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.websystemdesign.model.Ruolo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DipendenteDto {
    private Long id;

    @NotNull(message = "Il ruolo è obbligatorio")
    private Ruolo ruolo;

    @NotNull(message = "La sede è obbligatoria")
    private Long sedeId;
    
    private Long utenteId;

    // Campi per la visualizzazione e creazione
    private String nome;
    private String cognome;
    private String username;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
    private String password; // Aggiunto per la creazione

    private String nomeSede;
}
