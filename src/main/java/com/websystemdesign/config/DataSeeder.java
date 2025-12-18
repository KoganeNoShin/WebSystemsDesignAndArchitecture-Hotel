package com.websystemdesign.config;

import com.websystemdesign.model.*;
import com.websystemdesign.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
        // Qui potremmo fare una findByName, ma per semplicità nel seeder assumiamo che se count==0 creiamo tutto
        // Dato che siamo dentro l'if(count==0), creiamo direttamente.
        Sede sede = new Sede(nome, location, tassa);
        return sedeRepository.save(sede);
    }
    
    // Metodo helper per evitare duplicati sui servizi (che hanno vincolo unique)
    private void createServiceIfNotFound(String nome, float costo) {
        // Nota: ServiceRepository non ha findByNome di default, dovremmo aggiungerlo o usare Example.
        // Per ora, dato che il blocco principale è if(sedeRepository.count() == 0), 
        // assumiamo che se non ci sono sedi, non ci sono nemmeno servizi.
        // Ma per sicurezza contro riavvii parziali:
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
        // Camera Singola
        Camera c1 = new Camera(sede, 1, StatoCamera.LIBERA);
        c1.setLuce(false);
        c1.setTapparelle(true);
        c1.setTemperatura(20.0f);

        // Camera Doppia
        Camera c2 = new Camera(sede, 2, StatoCamera.LIBERA);
        c2.setLuce(true);
        c2.setTapparelle(false);
        c2.setTemperatura(21.5f);

        // Suite (4 posti)
        Camera c3 = new Camera(sede, 4, StatoCamera.OCCUPATA);
        c3.setLuce(true);
        c3.setTapparelle(true);
        c3.setTemperatura(22.0f);

        cameraRepository.saveAll(Arrays.asList(c1, c2, c3));
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
