package com.websystemdesign.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column // Nullable by default
    private String cittadinanza;

    @Column
    private String luogo;

    @Column
    private String dataNascita;

    @Column
    private String tipoDocumento;

    @Column
    private String numDocumento;

    @NonNull
    @OneToOne
    @JoinColumn(name = "ref_utente", referencedColumnName = "id", unique = true, nullable = false)
    private Utente utente;

    @OneToMany(mappedBy = "cliente")
    @ToString.Exclude
    private Set<Prenotazione> prenotazioni;
    
    // Costruttore completo per il seeder (opzionale, ma utile per non rompere il codice esistente)
    public Cliente(String cittadinanza, String luogo, String dataNascita, String tipoDocumento, String numDocumento, Utente utente) {
        this.cittadinanza = cittadinanza;
        this.luogo = luogo;
        this.dataNascita = dataNascita;
        this.tipoDocumento = tipoDocumento;
        this.numDocumento = numDocumento;
        this.utente = utente;
    }
}
