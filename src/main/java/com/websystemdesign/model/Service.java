package com.websystemdesign.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true, nullable = false)
    private String nome;

    @NonNull
    @Column(nullable = false)
    private float costo;

    @ManyToMany(mappedBy = "services")
    @ToString.Exclude
    private Set<Sede> sedi;

    @ManyToMany(mappedBy = "services")
    @ToString.Exclude
    private Set<Prenotazione> prenotazioni;
}
