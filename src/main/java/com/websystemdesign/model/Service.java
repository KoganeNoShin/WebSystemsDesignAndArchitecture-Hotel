package com.websystemdesign.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    @Column(nullable = false)
    private float costo;

    @ManyToMany(mappedBy = "services")
    private Set<Sede> sedi;

    @ManyToMany(mappedBy = "services")
    private Set<Prenotazione> prenotazioni;

    // Costruttori
    public Service() {
    }

    public Service(String nome, float costo) {
        this.nome = nome;
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

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public Set<Sede> getSedi() {
        return sedi;
    }

    public void setSedi(Set<Sede> sedi) {
        this.sedi = sedi;
    }

    public Set<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public void setPrenotazioni(Set<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }
}
