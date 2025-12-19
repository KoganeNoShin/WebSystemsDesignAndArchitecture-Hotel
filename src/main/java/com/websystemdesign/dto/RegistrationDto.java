package com.websystemdesign.dto;

import com.websystemdesign.model.TipoDocumento;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RegistrationDto {

    // --- Dati Utente ---
    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "L'username è obbligatorio")
    @Size(min = 3, max = 20, message = "L'username deve avere tra 3 e 20 caratteri")
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
             message = "La password deve contenere almeno un numero, una maiuscola, una minuscola, un carattere speciale e essere lunga almeno 8 caratteri.")
    private String password;

    // --- Dati Cliente ---
    @NotBlank(message = "La cittadinanza è obbligatoria")
    private String cittadinanza;

    @NotBlank(message = "Il luogo di nascita è obbligatorio")
    private String luogoNascita;

    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data di nascita deve essere nel passato")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascita;

    @NotNull(message = "Il tipo di documento è obbligatorio")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "Il numero del documento è obbligatorio")
    @Size(min = 5, message = "Il numero del documento sembra troppo corto")
    private String numDocumento;
}
