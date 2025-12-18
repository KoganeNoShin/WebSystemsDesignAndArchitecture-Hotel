package com.websystemdesign.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "dipendente")
public class Dipendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ruolo ruolo;

    @ManyToOne
    @JoinColumn(name = "ref_sede")
    private Sede sede;

    @NonNull
    @OneToOne
    @JoinColumn(name = "ref_utente", referencedColumnName = "id", unique = true, nullable = false)
    private Utente utente;
}
