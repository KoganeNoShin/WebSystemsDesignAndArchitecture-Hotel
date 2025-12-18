package com.websystemdesign.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "sede")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String nome;

    @NonNull
    @Column(nullable = false)
    private String location;

    @NonNull
    @Column(nullable = false)
    private String tassaSoggiorno;

    @OneToMany(mappedBy = "sede")
    @ToString.Exclude // Evita la ricorsione infinita
    private Set<Camera> camere;

    @OneToMany(mappedBy = "sede")
    @ToString.Exclude
    private Set<Dipendente> dipendenti;

    @ManyToMany
    @JoinTable(
            name = "sede_service",
            joinColumns = @JoinColumn(name = "ref_sede"),
            inverseJoinColumns = @JoinColumn(name = "ref_service")
    )
    @ToString.Exclude
    private Set<Service> services;
}
