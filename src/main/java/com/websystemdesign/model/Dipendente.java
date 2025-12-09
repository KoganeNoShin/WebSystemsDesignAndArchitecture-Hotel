package com.websystemdesign.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dipendente")
public class Dipendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ruolo ruolo;

    @ManyToOne
    @JoinColumn(name = "ref_sede")
    private Sede sede;

    @OneToOne
    @JoinColumn(name = "ref_utente", referencedColumnName = "id", unique = true, nullable = false)
    private Utente utente;

    // Costruttori
    public Dipendente() {
    }

    public Dipendente(Ruolo ruolo, Sede sede, Utente utente) {
        this.ruolo = ruolo;
        this.sede = sede;
        this.utente = utente;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}
