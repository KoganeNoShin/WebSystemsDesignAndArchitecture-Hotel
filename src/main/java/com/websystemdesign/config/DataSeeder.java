package com.websystemdesign.config;

import com.websystemdesign.model.*;
import com.websystemdesign.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    private final SedeRepository sedeRepository;
    private final CameraRepository cameraRepository;
    private final ServiceRepository serviceRepository;
    private final MultimediaRepository multimediaRepository;
    private final UtenteRepository utenteRepository;
    private final DipendenteRepository dipendenteRepository;
    private final ClienteRepository clienteRepository;
    private final PrenotazioneRepository prenotazioneRepository; // Aggiunto per pulizia
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
        System.out.println("Pulizia completa del Database...");
        
        // Cancelliamo i dati in ordine inverso per rispettare i vincoli di chiave esterna
        // Nota: Grazie al CascadeType.ALL su Prenotazione, Note e Ospiti vengono cancellati automaticamente
        prenotazioneRepository.deleteAll(); 
        
        cameraRepository.deleteAll();
        dipendenteRepository.deleteAll();
        clienteRepository.deleteAll();
        sedeRepository.deleteAll();
        utenteRepository.deleteAll();
        serviceRepository.deleteAll();
        multimediaRepository.deleteAll();

        System.out.println("Database pulito. Inizio Data Seeding...");

        // 1. Creazione Sedi
        Sede sedeCortina = new Sede("Alpine Palace Cortina", "Cortina d'Ampezzo", "5.00");
        Sede sedeRoma = new Sede("Urban Luxury Roma", "Roma Centro", "7.50");
        sedeRepository.saveAll(Arrays.asList(sedeCortina, sedeRoma));

        // 2. Creazione Camere
        createCamereForSede(sedeCortina);
        createCamereForSede(sedeRoma);

        // 3. Creazione Servizi
        Service spa = new Service("Accesso SPA", 50.0f);
        Service colazione = new Service("Colazione in camera", 15.0f);
        Service navetta = new Service("Navetta Aeroportuale", 30.0f);
        serviceRepository.saveAll(Arrays.asList(spa, colazione, navetta));

        // 4. Creazione Multimedia (Il costruttore Lombok prende solo i campi @NonNull: nome e costo)
        Multimedia film = new Multimedia("Film Prima Visione", 5.0f);
        Multimedia musica = new Multimedia("Playlist Relax", 2.0f);
        multimediaRepository.saveAll(Arrays.asList(film, musica));

        // 5. Creazione Utenti e Ruoli
        seedUsers(sedeCortina);

        System.out.println("Data Seeding completato con successo!");
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
        Utente adminUser = new Utente("admin", passwordEncoder.encode("admin"), "Admin", "Superuser");
        utenteRepository.save(adminUser);

        // Dipendente: costruttore Lombok (Ruolo, Utente). La sede è opzionale nel costruttore se non è @NonNull, 
        // ma nel nostro model Dipendente la sede non è @NonNull, quindi usiamo il setter.
        Dipendente adminDipendente = new Dipendente(Ruolo.AMMINISTRATORE, adminUser);
        adminDipendente.setSede(sedeDiLavoro);
        dipendenteRepository.save(adminDipendente);

        // --- STAFF ---
        Utente staffUser = new Utente("staff", passwordEncoder.encode("staff"), "Luigi", "Verdi");
        utenteRepository.save(staffUser);

        Dipendente staffDipendente = new Dipendente(Ruolo.STAFF, staffUser);
        staffDipendente.setSede(sedeDiLavoro);
        dipendenteRepository.save(staffDipendente);

        // --- CLIENTE ---
        Utente clienteUser = new Utente("mario", passwordEncoder.encode("password"), "Mario", "Rossi");
        utenteRepository.save(clienteUser);

        Cliente cliente = new Cliente("Italiana", "Milano", "1990-01-01", "Carta d'Identità", "AX123456", clienteUser);
        clienteRepository.save(cliente);
    }
}
