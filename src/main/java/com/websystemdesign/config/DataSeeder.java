package com.websystemdesign.config;

import com.websystemdesign.model.*;
import com.websystemdesign.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final SedeRepository sedeRepository;
    private final CameraRepository cameraRepository;
    private final ServiceRepository serviceRepository;
    private final MultimediaRepository multimediaRepository;
    private final UtenteRepository utenteRepository;
    private final DipendenteRepository dipendenteRepository;
    private final ClienteRepository clienteRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(SedeRepository sedeRepository,
                      CameraRepository cameraRepository,
                      ServiceRepository serviceRepository,
                      MultimediaRepository multimediaRepository,
                      UtenteRepository utenteRepository,
                      DipendenteRepository dipendenteRepository,
                      ClienteRepository clienteRepository,
                      PrenotazioneRepository prenotazioneRepository,
                      PasswordEncoder passwordEncoder) {
        this.sedeRepository = sedeRepository;
        this.cameraRepository = cameraRepository;
        this.serviceRepository = serviceRepository;
        this.multimediaRepository = multimediaRepository;
        this.utenteRepository = utenteRepository;
        this.dipendenteRepository = dipendenteRepository;
        this.clienteRepository = clienteRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Inizio Data Seeding...");
        
        // Pulizia relazioni
        prenotazioneRepository.deleteAll();
        clienteRepository.deleteAll();
        dipendenteRepository.deleteAll();
        utenteRepository.deleteAll();
        cameraRepository.deleteAll();
        
        List<Sede> allSedi = sedeRepository.findAll();
        for(Sede s : allSedi) {
            s.setServices(null);
            sedeRepository.save(s);
        }
        
        sedeRepository.deleteAll();
        serviceRepository.deleteAll();
        multimediaRepository.deleteAll();

        if (sedeRepository.count() == 0) {
            System.out.println("Database vuoto. Inizio Data Seeding...");

            // 1. Sedi
            Sede sedeCortina = createSedeIfNotFound("Alpine Palace Cortina", "Cortina d'Ampezzo", "5.00");
            Sede sedeRoma = createSedeIfNotFound("Urban Luxury Roma", "Roma Centro", "7.50");

            // 2. Servizi
            Service spa = createService("Accesso SPA", 50.0f);
            Service colazione = createService("Colazione in camera", 15.0f);
            Service navetta = createService("Navetta Aeroportuale", 30.0f);
            Service tour = createService("Tour Guidato", 40.0f);
            Service sci = createService("Noleggio Sci", 25.0f);

            addServiceToSede(sedeCortina, spa);
            addServiceToSede(sedeCortina, colazione);
            addServiceToSede(sedeCortina, sci);
            addServiceToSede(sedeRoma, colazione);
            addServiceToSede(sedeRoma, navetta);
            addServiceToSede(sedeRoma, tour);
            
            sedeRepository.save(sedeCortina);
            sedeRepository.save(sedeRoma);

            // 3. Camere
            List<Camera> camereCortina = createCamereForSede(sedeCortina);
            List<Camera> camereRoma = createCamereForSede(sedeRoma);

            // 4. Multimedia
            createMultimediaIfNotFound("Film Prima Visione", 5.0f);
            createMultimediaIfNotFound("Playlist Relax", 2.0f);

            // 5. Utenti
            seedUsers(sedeCortina);
            
            // 6. Prenotazioni di Test per Mario
            Utente marioUser = utenteRepository.findByUsername("mario").orElseThrow();
            Cliente mario = clienteRepository.findByUtenteId(marioUser.getId()).orElseThrow();
            
            createPrenotazioneTest(mario, camereCortina.get(0), LocalDate.now().plusDays(2), LocalDate.now().plusDays(5));
            createPrenotazioneTest(mario, camereCortina.get(1), LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));

            // 7. Utente Diego (Richiesto)
            if (utenteRepository.findByUsername("Diego").isEmpty()) {
                Utente diegoUser = new Utente("Diego", passwordEncoder.encode("password"), "Diego", "Bianchi");
                utenteRepository.save(diegoUser);

                Cliente diego = new Cliente("Italiana", "Roma", "1995-05-05", "Patente", "RM987654", diegoUser);
                clienteRepository.save(diego);
                
                // 4 Prenotazioni Passate
                createPrenotazioneTest(diego, camereRoma.get(0), LocalDate.now().minusDays(30), LocalDate.now().minusDays(25));
                createPrenotazioneTest(diego, camereCortina.get(3), LocalDate.now().minusDays(60), LocalDate.now().minusDays(55));
                createPrenotazioneTest(diego, camereRoma.get(2), LocalDate.now().minusDays(100), LocalDate.now().minusDays(95));
                createPrenotazioneTest(diego, camereCortina.get(4), LocalDate.now().minusDays(150), LocalDate.now().minusDays(145));
                
                // 1 Prenotazione Futura
                createPrenotazioneTest(diego, camereCortina.get(2), LocalDate.now().plusDays(20), LocalDate.now().plusDays(25));
            }

            // 8. Utente Simone (Richiesto)
            if (utenteRepository.findByUsername("Simone").isEmpty()) {
                Utente simoneUser = new Utente("Simone", passwordEncoder.encode("password"), "Simone", "Verdi");
                utenteRepository.save(simoneUser);

                Cliente simone = new Cliente("Italiana", "Milano", "1992-02-02", "Carta d'Identità", "MI123456", simoneUser);
                clienteRepository.save(simone);
                
                // Stesse prenotazioni di Diego
                createPrenotazioneTest(simone, camereRoma.get(0), LocalDate.now().minusDays(30), LocalDate.now().minusDays(25));
                createPrenotazioneTest(simone, camereCortina.get(3), LocalDate.now().minusDays(60), LocalDate.now().minusDays(55));
                createPrenotazioneTest(simone, camereRoma.get(2), LocalDate.now().minusDays(100), LocalDate.now().minusDays(95));
                createPrenotazioneTest(simone, camereCortina.get(4), LocalDate.now().minusDays(150), LocalDate.now().minusDays(145));
                createPrenotazioneTest(simone, camereCortina.get(2), LocalDate.now().plusDays(20), LocalDate.now().plusDays(25));
            }

            System.out.println("Data Seeding completato con successo!");
        } else {
            System.out.println("Database già popolato. Seeding saltato.");
        }
    }

    private Sede createSedeIfNotFound(String nome, String location, String tassa) {
        Sede sede = new Sede(nome, location, tassa);
        return sedeRepository.save(sede);
    }
    
    private Service createService(String nome, float costo) {
        Service s = new Service(nome, costo);
        return serviceRepository.save(s);
    }
    
    private void addServiceToSede(Sede sede, Service service) {
        if (sede.getServices() == null) {
            sede.setServices(new HashSet<>());
        }
        sede.getServices().add(service);
    }

    private void createMultimediaIfNotFound(String nome, float costo) {
        Multimedia m = new Multimedia(nome, costo);
        multimediaRepository.save(m);
    }

    private List<Camera> createCamereForSede(Sede sede) {
        List<Camera> camere = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Camera c = new Camera(sede, 2, StatoCamera.LIBERA, "10" + i, 100.0f + (i * 10));
            c.setLuce(false);
            c.setTapparelle(true);
            c.setTemperatura(20.0f);
            c.setTipologia("Standard");
            camere.add(c);
        }

        for (int i = 1; i <= 5; i++) {
            Camera c = new Camera(sede, 4, StatoCamera.LIBERA, "20" + i, 250.0f + (i * 20));
            c.setLuce(true);
            c.setTapparelle(true);
            c.setTemperatura(22.0f);
            c.setTipologia("Suite");
            camere.add(c);
        }

        return cameraRepository.saveAll(camere);
    }
    
    private void createPrenotazioneTest(Cliente cliente, Camera camera, LocalDate start, LocalDate end) {
        Prenotazione p = new Prenotazione();
        p.setCliente(cliente);
        p.setCamera(camera);
        p.setDataInizio(start);
        p.setDataFine(end);
        p.setCosto(camera.getPrezzoBase() * (end.toEpochDay() - start.toEpochDay()));
        p.setStato(StatoPrenotazione.CONFERMATA);
        prenotazioneRepository.save(p);
    }

    private void seedUsers(Sede sedeDiLavoro) {
        if (utenteRepository.findByUsername("admin").isEmpty()) {
            Utente adminUser = new Utente("admin", passwordEncoder.encode("admin"), "Admin", "Superuser");
            utenteRepository.save(adminUser);
            Dipendente adminDipendente = new Dipendente(Ruolo.AMMINISTRATORE, adminUser);
            adminDipendente.setSede(sedeDiLavoro);
            dipendenteRepository.save(adminDipendente);
        }

        if (utenteRepository.findByUsername("staff").isEmpty()) {
            Utente staffUser = new Utente("staff", passwordEncoder.encode("staff"), "Luigi", "Verdi");
            utenteRepository.save(staffUser);
            Dipendente staffDipendente = new Dipendente(Ruolo.STAFF, staffUser);
            staffDipendente.setSede(sedeDiLavoro);
            dipendenteRepository.save(staffDipendente);
        }

        if (utenteRepository.findByUsername("mario").isEmpty()) {
            Utente clienteUser = new Utente("mario", passwordEncoder.encode("password"), "Mario", "Rossi");
            utenteRepository.save(clienteUser);
            Cliente cliente = new Cliente("Italiana", "Milano", "1990-01-01", "Carta d'Identità", "AX123456", clienteUser);
            clienteRepository.save(cliente);
        }
    }
}
