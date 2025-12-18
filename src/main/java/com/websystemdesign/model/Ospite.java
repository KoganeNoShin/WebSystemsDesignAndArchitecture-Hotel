package com.websystemdesign.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "ospite")
public class Ospite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String nome;

    @NonNull
    @Column(nullable = false)
    private String cognome;

    @NonNull
    @Column(nullable = false)
    private String cittadinanza;

    @NonNull
    @Column(nullable = false)
    private String luogo;

    @NonNull
    @Column(nullable = false)
    private LocalDate dataNascita;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "ref_prenotazione", nullable = false)
    private Prenotazione prenotazione;
}
