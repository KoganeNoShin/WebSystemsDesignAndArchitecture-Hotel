package com.websystemdesign.model;

import jakarta.persistence.*;

@Entity
@Table(name = "note")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nota;

    @ManyToOne
    @JoinColumn(name = "ref_prenotazione", nullable = false)
    private Prenotazione prenotazione;

    // Costruttori
    public Nota() {
    }

    public Nota(String nota, Prenotazione prenotazione) {
        this.nota = nota;
        this.prenotazione = prenotazione;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Prenotazione getPrenotazione() {
        return prenotazione;
    }

    public void setPrenotazione(Prenotazione prenotazione) {
        this.prenotazione = prenotazione;
    }
}
