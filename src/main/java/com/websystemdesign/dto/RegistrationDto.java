package com.websystemdesign.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationDto {

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
}
