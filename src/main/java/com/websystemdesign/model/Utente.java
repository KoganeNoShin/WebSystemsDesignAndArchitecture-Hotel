package com.websystemdesign.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utente")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L''username non può essere vuoto")
    @Size(min = 3, max = 20, message = "L''username deve avere tra 3 e 20 caratteri")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "La password non può essere vuota")
    @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Il nome non può essere vuoto")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Il cognome non può essere vuoto")
    @Column(nullable = false)
    private String cognome;

    public Utente(String username, String password, String nome, String cognome) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
    }
}
