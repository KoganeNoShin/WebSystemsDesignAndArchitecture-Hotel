package com.websystemdesign.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    // Costruttore vuoto per JPA
    public Utente() {
    }

    // Costruttore con parametri
    public Utente(String username, String password, String nome, String cognome) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
}
