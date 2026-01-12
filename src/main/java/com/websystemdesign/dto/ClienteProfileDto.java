package com.websystemdesign.dto;

import com.websystemdesign.model.TipoDocumento;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ClienteProfileDto {

    private String nome;
    private String cognome;
    private String username;

    private String cittadinanza;

    private String luogoNascita;

    @Past(message = "La data di nascita deve essere nel passato")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascita;

    private TipoDocumento tipoDocumento;

    private String numDocumento;
}
