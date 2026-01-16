package com.websystemdesign.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websystemdesign.model.*;
import com.websystemdesign.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
    private final OspiteRepository ospiteRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper; // Per leggere il JSON

    public DataSeeder(SedeRepository sedeRepository,
                      CameraRepository cameraRepository,
                      ServiceRepository serviceRepository,
                      MultimediaRepository multimediaRepository,
                      UtenteRepository utenteRepository,
                      DipendenteRepository dipendenteRepository,
                      ClienteRepository clienteRepository,
                      PrenotazioneRepository prenotazioneRepository,
                      OspiteRepository ospiteRepository,
                      PasswordEncoder passwordEncoder,
                      ObjectMapper objectMapper) {
        this.sedeRepository = sedeRepository;
        this.cameraRepository = cameraRepository;
        this.serviceRepository = serviceRepository;
        this.multimediaRepository = multimediaRepository;
        this.utenteRepository = utenteRepository;
        this.dipendenteRepository = dipendenteRepository;
        this.clienteRepository = clienteRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.ospiteRepository = ospiteRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(false) {
            System.out.println("Inizio Data Seeding...");

            ospiteRepository.deleteAll();
            prenotazioneRepository.deleteAll();
            clienteRepository.deleteAll();
            dipendenteRepository.deleteAll();
            utenteRepository.deleteAll();
            cameraRepository.deleteAll();

            List<Sede> allSedi = sedeRepository.findAll();
            for (Sede s : allSedi) {
                s.setServices(null);
                sedeRepository.save(s);
            }

            sedeRepository.deleteAll();
            serviceRepository.deleteAll();
            multimediaRepository.deleteAll();
        }
        if (sedeRepository.count() == 0) {
            System.out.println("Database vuoto. Inizio Data Seeding...");

            Sede sedeCortina = createSedeIfNotFound("Alpine Palace Cortina", "Cortina d'Ampezzo", "5.00");
            Sede sedeRoma = createSedeIfNotFound("Urban Luxury Roma", "Roma Centro", "7.50");

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

            List<Camera> camereCortina = createCamereForSede(sedeCortina);
            List<Camera> camereRoma = createCamereForSede(sedeRoma);

            loadMultimediaFromJson();

            seedUsers(sedeCortina);

            Utente marioUser = utenteRepository.findByUsername("mario").orElseThrow();
            Cliente mario = clienteRepository.findByUtenteId(marioUser.getId()).orElseThrow();
            
            createPrenotazioneTest(mario, camereCortina.get(0), LocalDate.now().plusDays(2), LocalDate.now().plusDays(5), StatoPrenotazione.CONFERMATA);
            createPrenotazioneTest(mario, camereCortina.get(1), LocalDate.now().plusDays(10), LocalDate.now().plusDays(15), StatoPrenotazione.CONFERMATA);

            if (utenteRepository.findByUsername("Diego").isEmpty()) {
                Utente diegoUser = new Utente("Diego", passwordEncoder.encode("password"), "Diego", "Corona");
                utenteRepository.save(diegoUser);

                Cliente diego = new Cliente("Italiana", "Roma", "1995-05-05", "Patente", "RM987654", diegoUser);
                clienteRepository.save(diego);

                createPrenotazioneTest(diego, camereRoma.get(0), LocalDate.now().minusDays(30), LocalDate.now().minusDays(25), StatoPrenotazione.CHECKED_OUT);
                createPrenotazioneTest(diego, camereCortina.get(3), LocalDate.now().minusDays(60), LocalDate.now().minusDays(55), StatoPrenotazione.CHECKED_OUT);
                createPrenotazioneTest(diego, camereRoma.get(2), LocalDate.now().minusDays(100), LocalDate.now().minusDays(95), StatoPrenotazione.CHECKED_OUT);
                createPrenotazioneTest(diego, camereCortina.get(4), LocalDate.now().minusDays(150), LocalDate.now().minusDays(145), StatoPrenotazione.CHECKED_OUT);

                Camera cameraAttivaDiego = camereCortina.get(2);
                cameraAttivaDiego.setStatus(StatoCamera.OCCUPATA);
                cameraRepository.save(cameraAttivaDiego);
                
                Prenotazione pAttiva = createPrenotazioneTest(diego, cameraAttivaDiego, LocalDate.now().minusDays(2), LocalDate.now().plusDays(5), StatoPrenotazione.CHECKED_IN);

                Ospite giulia = new Ospite();
                giulia.setPrenotazione(pAttiva);
                giulia.setNome("Giulia");
                giulia.setCognome("Greco");
                giulia.setCittadinanza("Italiana");
                giulia.setLuogo("Napoli");
                giulia.setDataNascita(LocalDate.of(1996, 6, 6));
                ospiteRepository.save(giulia);
                
                pAttiva.setNumeroOspiti(2);

                List<Multimedia> films = multimediaRepository.findAll();
                if (!films.isEmpty()) {
                    pAttiva.setMultimedia(new HashSet<>());
                    pAttiva.getMultimedia().add(films.get(0)); // Aggiunge il primo film
                    pAttiva.setCosto(pAttiva.getCosto() + films.get(0).getCosto()); // Aggiorna costo
                }
                
                prenotazioneRepository.save(pAttiva);
            }

            if (utenteRepository.findByUsername("Simone").isEmpty()) {
                Utente simoneUser = new Utente("Simone", passwordEncoder.encode("password"), "Simone", "Comitini");
                utenteRepository.save(simoneUser);

                Cliente simone = new Cliente("Italiana", "Milano", "1992-02-02", "Carta d'Identità", "MI123456", simoneUser);
                clienteRepository.save(simone);
                
                createPrenotazioneTest(simone, camereRoma.get(0), LocalDate.now().minusDays(30), LocalDate.now().minusDays(25), StatoPrenotazione.CHECKED_OUT);
                createPrenotazioneTest(simone, camereCortina.get(3), LocalDate.now().minusDays(60), LocalDate.now().minusDays(55), StatoPrenotazione.CHECKED_OUT);
                createPrenotazioneTest(simone, camereRoma.get(2), LocalDate.now().minusDays(100), LocalDate.now().minusDays(95), StatoPrenotazione.CHECKED_OUT);
                createPrenotazioneTest(simone, camereCortina.get(4), LocalDate.now().minusDays(150), LocalDate.now().minusDays(145), StatoPrenotazione.CHECKED_OUT);
                createPrenotazioneTest(simone, camereCortina.get(2), LocalDate.now().plusDays(20), LocalDate.now().plusDays(25), StatoPrenotazione.CONFERMATA);
            }

            System.out.println("Data Seeding completato con successo!");
        } else {
            System.out.println("Database già popolato. Seeding saltato.");
        }
    }

    private void loadMultimediaFromJson() {
        try {
            File jsonFile = new ClassPathResource("static/json/catalogo_film.json").getFile();
            List<Map<String, Object>> films = objectMapper.readValue(jsonFile, new TypeReference<>() {});
            
            for (Map<String, Object> filmData : films) {
                String titolo = (String) filmData.get("titolo");
                Object prezzoObj = filmData.get("prezzo");
                float prezzo = 0.0f;
                if (prezzoObj instanceof Double) {
                    prezzo = ((Double) prezzoObj).floatValue();
                } else if (prezzoObj instanceof Integer) {
                    prezzo = ((Integer) prezzoObj).floatValue();
                }
                
                Multimedia m = new Multimedia(titolo, prezzo);
                m.setImmagine((String) filmData.get("poster_url"));
                m.setDescrizione((String) filmData.get("descrizione"));
                
                Object votoObj = filmData.get("voto");
                if (votoObj instanceof Double) {
                    m.setVoto((Double) votoObj);
                } else if (votoObj instanceof Integer) {
                    m.setVoto(((Integer) votoObj).doubleValue());
                }

                multimediaRepository.save(m);
            }
            System.out.println("Caricati " + films.size() + " film dal JSON.");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del catalogo film: " + e.getMessage());
            createMultimediaIfNotFound("Film Prima Visione", 5.0f);
            createMultimediaIfNotFound("Playlist Relax", 2.0f);
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
    
    private Prenotazione createPrenotazioneTest(Cliente cliente, Camera camera, LocalDate start, LocalDate end, StatoPrenotazione stato) {
        Prenotazione p = new Prenotazione();
        p.setCliente(cliente);
        p.setCamera(camera);
        p.setDataInizio(start);
        p.setDataFine(end);
        p.setCosto(camera.getPrezzoBase() * (end.toEpochDay() - start.toEpochDay()));
        p.setStato(stato);
        p.setNumeroOspiti(1);
        Prenotazione saved = prenotazioneRepository.save(p);
        
        if (stato == StatoPrenotazione.CHECKED_OUT || stato == StatoPrenotazione.CHECKED_IN) {
            Ospite ospite = new Ospite();
            ospite.setPrenotazione(saved);
            ospite.setNome(cliente.getUtente().getNome());
            ospite.setCognome(cliente.getUtente().getCognome());
            ospite.setCittadinanza(cliente.getCittadinanza());
            ospite.setLuogo(cliente.getLuogo());
            if (cliente.getDataNascita() != null) {
                ospite.setDataNascita(LocalDate.parse(cliente.getDataNascita()));
            } else {
                ospite.setDataNascita(LocalDate.of(1990, 1, 1));
            }
            ospiteRepository.save(ospite);
        }
        return saved;
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
