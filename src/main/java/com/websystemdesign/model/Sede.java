package com.websystemdesign.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "sede")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String tassaSoggiorno;

    @OneToMany(mappedBy = "sede")
    private Set<Camera> camere;

    @OneToMany(mappedBy = "sede")
    private Set<Dipendente> dipendenti;

    @ManyToMany
    @JoinTable(
            name = "sede_service",
            joinColumns = @JoinColumn(name = "ref_sede"),
            inverseJoinColumns = @JoinColumn(name = "ref_service")
    )
    private Set<Service> services;


    // Costruttori
    public Sede() {
    }

    public Sede(String nome, String location, String tassaSoggiorno) {
        this.nome = nome;
        this.location = location;
        this.tassaSoggiorno = tassaSoggiorno;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTassaSoggiorno() {
        return tassaSoggiorno;
    }

    public void setTassaSoggiorno(String tassaSoggiorno) {
        this.tassaSoggiorno = tassaSoggiorno;
    }

    public Set<Camera> getCamere() {
        return camere;
    }

    public void setCamere(Set<Camera> camere) {
        this.camere = camere;
    }

    public Set<Dipendente> getDipendenti() {
        return dipendenti;
    }

    public void setDipendenti(Set<Dipendente> dipendenti) {
        this.dipendenti = dipendenti;
    }

    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }
}
