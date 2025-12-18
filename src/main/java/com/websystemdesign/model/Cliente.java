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

    @NonNull
    @Column(nullable = false)
    private String cittadinanza;

    @NonNull
    @Column(nullable = false)
    private String luogo;

    @NonNull
    @Column(nullable = false)
    private String dataNascita;

    @NonNull
    @Column(nullable = false)
    private String tipoDocumento;

    @NonNull
    @Column(nullable = false)
    private String numDocumento;

    @NonNull
    @OneToOne
    @JoinColumn(name = "ref_utente", referencedColumnName = "id", unique = true, nullable = false)
    private Utente utente;

    @OneToMany(mappedBy = "cliente")
    @ToString.Exclude
    private Set<Prenotazione> prenotazioni;
}
