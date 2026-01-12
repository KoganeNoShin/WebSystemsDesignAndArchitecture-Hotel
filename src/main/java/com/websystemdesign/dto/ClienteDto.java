package com.websystemdesign.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteDto {
    private Long id;

    @NotBlank(message = "La cittadinanza è obbligatoria")
    private String cittadinanza;

    @NotBlank(message = "Il luogo di nascita è obbligatorio")
    private String luogo;

    @NotBlank(message = "La data di nascita è obbligatoria")
    private String dataNascita;

    @NotBlank(message = "Il tipo di documento è obbligatorio")
    private String tipoDocumento;

    @NotBlank(message = "Il numero del documento è obbligatorio")
    private String numDocumento;

    private Long utenteId;
}
