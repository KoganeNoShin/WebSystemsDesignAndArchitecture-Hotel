package com.websystemdesign.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "prenotazione")
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "ref_cliente", nullable = false)
    private Cliente cliente;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "ref_camera", nullable = false)
    private Camera camera;

    @NonNull
    @Column(nullable = false)
    private LocalDate dataInizio;

    @NonNull
    @Column(nullable = false)
    private LocalDate dataFine;

    @NonNull
    @Column(nullable = false)
    private float costo;

    @OneToMany(mappedBy = "prenotazione", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Nota> note;

    @OneToMany(mappedBy = "prenotazione", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Ospite> ospiti;

    @ManyToMany
    @JoinTable(
            name = "prenotazione_service",
            joinColumns = @JoinColumn(name = "ref_prenotazione"),
            inverseJoinColumns = @JoinColumn(name = "ref_service")
    )
    @ToString.Exclude
    private Set<Service> services;

    @ManyToMany
    @JoinTable(
            name = "prenotazione_multimedia",
            joinColumns = @JoinColumn(name = "ref_prenotazione"),
            inverseJoinColumns = @JoinColumn(name = "ref_multimedia")
    )
    @ToString.Exclude
    private Set<Multimedia> multimedia;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoPrenotazione stato; // Per gestire Check-in/Check-out
}
