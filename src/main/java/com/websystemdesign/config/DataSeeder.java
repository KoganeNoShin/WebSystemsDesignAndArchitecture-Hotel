package com.websystemdesign.config;

import com.websystemdesign.model.*;
import com.websystemdesign.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final SedeRepository sedeRepository;
    private final CameraRepository cameraRepository;
    private final ServiceRepository serviceRepository;
    private final MultimediaRepository multimediaRepository;
    private final UtenteRepository utenteRepository;
    private final DipendenteRepository dipendenteRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(SedeRepository sedeRepository,
                      CameraRepository cameraRepository,
                      ServiceRepository serviceRepository,
                      MultimediaRepository multimediaRepository,
                      UtenteRepository utenteRepository,
                      DipendenteRepository dipendenteRepository,
                      ClienteRepository clienteRepository,
                      PasswordEncoder passwordEncoder) {
        this.sedeRepository = sedeRepository;
        this.cameraRepository = cameraRepository;
        this.serviceRepository = serviceRepository;
        this.multimediaRepository = multimediaRepository;
        this.utenteRepository = utenteRepository;
        this.dipendenteRepository = dipendenteRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Inizio Data Seeding...");
        // L'ordine di eliminazione è importante a causa delle chiavi esterne
        clienteRepository.deleteAll();
        dipendenteRepository.deleteAll();
        utenteRepository.deleteAll();
        cameraRepository.deleteAll();
        sedeRepository.deleteAll();
        serviceRepository.deleteAll();
        multimediaRepository.deleteAll();

        // Eseguiamo il seeding solo se non ci sono sedi (indicatore che il DB è vuoto o parziale)
        if (sedeRepository.count() == 0) {
            System.out.println("Database vuoto. Inizio Data Seeding...");

            // 1. Creazione Sedi
            Sede sedeCortina = createSedeIfNotFound("Alpine Palace Cortina", "Cortina d'Ampezzo", "5.00");
            Sede sedeRoma = createSedeIfNotFound("Urban Luxury Roma", "Roma Centro", "7.50");

            // 2. Creazione Camere (Solo se le sedi sono state appena create o non hanno camere)
            if (cameraRepository.count() == 0) {
                createCamereForSede(sedeCortina);
                createCamereForSede(sedeRoma);
            }

            // 3. Creazione Servizi
            createServiceIfNotFound("Accesso SPA", 50.0f);
            createServiceIfNotFound("Colazione in camera", 15.0f);
            createServiceIfNotFound("Navetta Aeroportuale", 30.0f);

            // 4. Creazione Multimedia
            createMultimediaIfNotFound("Film Prima Visione", 5.0f);
            createMultimediaIfNotFound("Playlist Relax", 2.0f);

            // 5. Creazione Utenti e Ruoli
            seedUsers(sedeCortina);

            System.out.println("Data Seeding completato con successo!");
        } else {
            System.out.println("Database già popolato. Seeding saltato.");
        }
    }

    private Sede createSedeIfNotFound(String nome, String location, String tassa) {
        Sede sede = new Sede(nome, location, tassa);
        return sedeRepository.save(sede);
    }
    
    private void createServiceIfNotFound(String nome, float costo) {
        Service s = new Service(nome, costo);
        try {
            serviceRepository.save(s);
        } catch (Exception e) {
            // Ignora se esiste già
        }
    }

    private void createMultimediaIfNotFound(String nome, float costo) {
        Multimedia m = new Multimedia(nome, costo);
        multimediaRepository.save(m);
    }

    private void createCamereForSede(Sede sede) {
        List<Camera> camere = new ArrayList<>();

        // 5 Camere Standard
        for (int i = 1; i <= 5; i++) {
            Camera c = new Camera(sede, 2, StatoCamera.LIBERA, "10" + i, 100.0f + (i * 10));
            c.setLuce(false);
            c.setTapparelle(true);
            c.setTemperatura(20.0f);
            c.setTipologia("Standard");
            camere.add(c);
        }

        // 5 Suite
        for (int i = 1; i <= 5; i++) {
            Camera c = new Camera(sede, 4, StatoCamera.LIBERA, "20" + i, 250.0f + (i * 20));
            c.setLuce(true);
            c.setTapparelle(true);
            c.setTemperatura(22.0f);
            c.setTipologia("Suite");
            camere.add(c);
        }

        cameraRepository.saveAll(camere);
    }

    private void seedUsers(Sede sedeDiLavoro) {
        // --- ADMIN ---
        if (utenteRepository.findByUsername("admin").isEmpty()) {
            Utente adminUser = new Utente("admin", passwordEncoder.encode("admin"), "Admin", "Superuser");
            utenteRepository.save(adminUser);

            Dipendente adminDipendente = new Dipendente(Ruolo.AMMINISTRATORE, adminUser);
            adminDipendente.setSede(sedeDiLavoro);
            dipendenteRepository.save(adminDipendente);
        }

        // --- STAFF ---
        if (utenteRepository.findByUsername("staff").isEmpty()) {
            Utente staffUser = new Utente("staff", passwordEncoder.encode("staff"), "Luigi", "Verdi");
            utenteRepository.save(staffUser);

            Dipendente staffDipendente = new Dipendente(Ruolo.STAFF, staffUser);
            staffDipendente.setSede(sedeDiLavoro);
            dipendenteRepository.save(staffDipendente);
        }

        // --- CLIENTE ---
        if (utenteRepository.findByUsername("mario").isEmpty()) {
            Utente clienteUser = new Utente("mario", passwordEncoder.encode("password"), "Mario", "Rossi");
            utenteRepository.save(clienteUser);

            Cliente cliente = new Cliente("Italiana", "Milano", "1990-01-01", "Carta d'Identità", "AX123456", clienteUser);
            clienteRepository.save(cliente);
        }
    }
}
