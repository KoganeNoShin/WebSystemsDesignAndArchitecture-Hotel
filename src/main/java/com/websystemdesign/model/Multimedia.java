package com.websystemdesign.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "multimedia")
public class Multimedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Lob
    private byte[] immagine;

    @Column(nullable = false)
    private float costo;

    @ManyToMany(mappedBy = "multimedia")
    private Set<Prenotazione> prenotazioni;

    // Costruttori
    public Multimedia() {
    }

    public Multimedia(String nome, byte[] immagine, float costo) {
        this.nome = nome;
        this.immagine = immagine;
        this.costo = costo;
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

    public byte[] getImmagine() {
        return immagine;
    }

    public void setImmagine(byte[] immagine) {
        this.immagine = immagine;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public Set<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public void setPrenotazioni(Set<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }
}
