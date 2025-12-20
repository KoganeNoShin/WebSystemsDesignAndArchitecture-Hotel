package com.websystemdesign.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "camera")
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "ref_sede", nullable = false)
    private Sede sede;

    @NonNull
    @Column(nullable = false)
    private int postiLetto;

    @NonNull
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
    @ToString.Exclude
    private Set<Prenotazione> prenotazioni;

    @NonNull
    @Column(nullable = false)
    private String numero; // Es. "101", "204B"

    @NonNull
    @Column(nullable = false)
    private float prezzoBase; // Prezzo per notte
}
