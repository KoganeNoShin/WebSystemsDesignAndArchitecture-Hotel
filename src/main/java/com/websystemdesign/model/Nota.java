package com.websystemdesign.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "note")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String nota;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "ref_prenotazione", nullable = false)
    private Prenotazione prenotazione;
}
