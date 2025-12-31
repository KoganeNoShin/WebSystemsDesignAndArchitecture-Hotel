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

    @NonNull
    @Column(nullable = false)
    private float costo;
    
    @Column
    private String immagine; // URL del poster

    @Column(length = 1000) // Descrizione più lunga
    private String descrizione;
    
    @Column
    private double voto;

    @ManyToMany(mappedBy = "multimedia")
    @ToString.Exclude
    private Set<Prenotazione> prenotazioni;
}
