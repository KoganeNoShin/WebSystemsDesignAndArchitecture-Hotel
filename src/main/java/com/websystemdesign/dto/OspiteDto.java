package com.websystemdesign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import java.time.LocalDate;

@Data
public class OspiteDto {
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "La cittadinanza è obbligatoria")
    private String cittadinanza;

    @NotBlank(message = "Il luogo di nascita è obbligatorio")
    private String luogo;

    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data di nascita deve essere nel passato")
    private LocalDate dataNascita;

    private Long prenotazioneId;
}
