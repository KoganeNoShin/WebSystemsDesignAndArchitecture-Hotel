package com.websystemdesign.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "multimedia")
public class Multimedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String nome;

    @Lob
    private byte[] immagine;

    @NonNull
    @Column(nullable = false)
    private float costo;

    @ManyToMany(mappedBy = "multimedia")
    @ToString.Exclude
    private Set<Prenotazione> prenotazioni;
}
