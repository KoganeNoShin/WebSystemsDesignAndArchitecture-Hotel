package com.websystemdesign.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "camera")
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ref_sede", nullable = false)
    private Sede sede;

    @Column(nullable = false)
    private int postiLetto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoCamera status;

    @Column(nullable = false)
    private boolean luce = false;

    @Column(nullable = false)
    private boolean tapparelle = false;

    @Column(nullable = false)
    private float temperatura = 18.0f;

    @OneToMany(mappedBy = "camera")
    private Set<Prenotazione> prenotazioni;

    // Costruttori
    public Camera() {
    }

    public Camera(Sede sede, int postiLetto, StatoCamera status) {
        this.sede = sede;
        this.postiLetto = postiLetto;
        this.status = status;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public int getPostiLetto() {
        return postiLetto;
    }

    public void setPostiLetto(int postiLetto) {
        this.postiLetto = postiLetto;
    }

    public StatoCamera getStatus() {
        return status;
    }

    public void setStatus(StatoCamera status) {
        this.status = status;
    }

    public boolean isLuce() {
        return luce;
    }

    public void setLuce(boolean luce) {
        this.luce = luce;
    }

    public boolean isTapparelle() {
        return tapparelle;
    }

    public void setTapparelle(boolean tapparelle) {
        this.tapparelle = tapparelle;
    }

    public float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(float temperatura) {
        this.temperatura = temperatura;
    }

    public Set<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public void setPrenotazioni(Set<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }
}
