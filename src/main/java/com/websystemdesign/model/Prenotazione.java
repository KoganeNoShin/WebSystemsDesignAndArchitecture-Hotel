package com.websystemdesign.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "prenotazione")
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ref_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "ref_camera", nullable = false)
    private Camera camera;

    @Column(nullable = false)
    private LocalDate dataInizio;

    @Column(nullable = false)
    private LocalDate dataFine;

    @Column(nullable = false)
    private float costo;

    @OneToMany(mappedBy = "prenotazione", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Nota> note;

    @OneToMany(mappedBy = "prenotazione", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ospite> ospiti;

    @ManyToMany
    @JoinTable(
            name = "prenotazione_service",
            joinColumns = @JoinColumn(name = "ref_prenotazione"),
            inverseJoinColumns = @JoinColumn(name = "ref_service")
    )
    private Set<Service> services;

    @ManyToMany
    @JoinTable(
            name = "prenotazione_multimedia",
            joinColumns = @JoinColumn(name = "ref_prenotazione"),
            inverseJoinColumns = @JoinColumn(name = "ref_multimedia")
    )
    private Set<Multimedia> multimedia;

    // Costruttori
    public Prenotazione() {
    }

    public Prenotazione(Cliente cliente, Camera camera, LocalDate dataInizio, LocalDate dataFine, float costo) {
        this.cliente = cliente;
        this.camera = camera;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.costo = costo;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public Set<Nota> getNote() {
        return note;
    }

    public void setNote(Set<Nota> note) {
        this.note = note;
    }

    public Set<Ospite> getOspiti() {
        return ospiti;
    }

    public void setOspiti(Set<Ospite> ospiti) {
        this.ospiti = ospiti;
    }

    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }

    public Set<Multimedia> getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(Set<Multimedia> multimedia) {
        this.multimedia = multimedia;
    }
}
