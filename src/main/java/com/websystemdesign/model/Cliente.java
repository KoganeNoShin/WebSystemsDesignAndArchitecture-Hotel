package com.websystemdesign.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cittadinanza;

    @Column(nullable = false)
    private String luogo;

    @Column(nullable = false)
    private String dataNascita;

    @Column(nullable = false)
    private String tipoDocumento;

    @Column(nullable = false)
    private String numDocumento;

    @OneToOne
    @JoinColumn(name = "ref_utente", referencedColumnName = "id", unique = true, nullable = false)
    private Utente utente;

    @OneToMany(mappedBy = "cliente")
    private Set<Prenotazione> prenotazioni;

    // Costruttori
    public Cliente() {
    }

    public Cliente(String cittadinanza, String luogo, String dataNascita, String tipoDocumento, String numDocumento, Utente utente) {
        this.cittadinanza = cittadinanza;
        this.luogo = luogo;
        this.dataNascita = dataNascita;
        this.tipoDocumento = tipoDocumento;
        this.numDocumento = numDocumento;
        this.utente = utente;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Set<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public void setPrenotazioni(Set<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }
}
