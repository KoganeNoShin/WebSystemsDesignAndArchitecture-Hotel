package com.websystemdesign.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "ospite")
public class Ospite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false)
    private String cittadinanza;

    @Column(nullable = false)
    private String luogo;

    @Column(nullable = false)
    private LocalDate dataNascita;

    @ManyToOne
    @JoinColumn(name = "ref_prenotazione", nullable = false)
    private Prenotazione prenotazione;

    // Costruttori
    public Ospite() {
    }

    public Ospite(String nome, String cognome, String cittadinanza, String luogo, LocalDate dataNascita, Prenotazione prenotazione) {
        this.nome = nome;
        this.cognome = cognome;
        this.cittadinanza = cittadinanza;
        this.luogo = luogo;
        this.dataNascita = dataNascita;
        this.prenotazione = prenotazione;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCittadinanza() {
        return cittadinanza;
    }

    public void setCittadinanza(String cittadinanza) {
        this.cittadinanza = cittadinanza;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public Prenotazione getPrenotazione() {
        return prenotazione;
    }

    public void setPrenotazione(Prenotazione prenotazione) {
        this.prenotazione = prenotazione;
    }
}
