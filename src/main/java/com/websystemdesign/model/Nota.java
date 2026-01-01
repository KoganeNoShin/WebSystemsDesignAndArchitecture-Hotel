package com.websystemdesign.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "nota")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false, length = 1000)
    private String testo;

    @Column(nullable = false)
    private LocalDateTime dataCreazione = LocalDateTime.now();

    @NonNull
    @ManyToOne
    @JoinColumn(name = "ref_prenotazione", nullable = false)
    private Prenotazione prenotazione;
}
