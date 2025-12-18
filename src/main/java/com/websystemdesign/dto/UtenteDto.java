package com.websystemdesign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // Annotazione di Lombok: genera getter, setter, toString, equals, hashCode
public class UtenteDto {

    private Long id;

    @NotBlank(message = "L'username non può essere vuoto")
    @Size(min = 3, max = 20, message = "L'username deve avere tra 3 e 20 caratteri")
    private String username;

    // La password viene gestita separatamente per la registrazione
    @NotBlank(message = "La password non può essere vuota")
    @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
    private String password;

    @NotBlank(message = "Il nome non può essere vuoto")
    private String nome;

    @NotBlank(message = "Il cognome non può essere vuoto")
    private String cognome;
}
