package com.websystemdesign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UtenteDto {

    private Long id;

    @NotBlank(message = "L'username non può essere vuoto")
    @Size(min = 3, max = 20, message = "L'username deve avere tra 3 e 20 caratteri")
    private String username;

    @NotBlank(message = "Il nome non può essere vuoto")
    private String nome;

    @NotBlank(message = "Il cognome non può essere vuoto")
    private String cognome;
}
