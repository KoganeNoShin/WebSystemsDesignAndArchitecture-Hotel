package com.websystemdesign.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websystemdesign.model.*;
import com.websystemdesign.repository.*;
import com.websystemdesign.scheduler.BookingCleanupScheduler;
import com.websystemdesign.scheduler.RoomScheduler;
import com.websystemdesign.service.ClienteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

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
    private final ObjectMapper objectMapper;
    private final ClienteService clienteService;
    private final BookingCleanupScheduler bookingCleanupScheduler;
    private final RoomScheduler roomScheduler;

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
                      ObjectMapper objectMapper,
                      ClienteService clienteService,
                      BookingCleanupScheduler bookingCleanupScheduler,
                      RoomScheduler roomScheduler) {
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
        this.clienteService = clienteService;
        this.bookingCleanupScheduler = bookingCleanupScheduler;
        this.roomScheduler = roomScheduler;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
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

            createAdmin();
            createStaff("staff1", "Staff", "Cortina", sedeCortina);
            createStaff("staff2", "Staff", "Roma", sedeRoma);

            List<Cliente> clienti = new ArrayList<>();
            Cliente diego = createCliente("diego", "Diego", "Corona", "2003-07-09");
            diego.setLuogo("Palermo");
            clienteRepository.save(diego);
            clienti.add(diego);

            Cliente simone = createCliente("simone", "Simone", "Comitini", "2003-01-21");
            simone.setLuogo("Palermo");
            clienteRepository.save(simone);
            clienti.add(simone);
            
            for (int i = 1; i <= 20; i++) {
                clienti.add(createCliente("cliente" + i, "Cliente", "Numero" + i, "1990-01-01"));
            }

            for (Cliente c : clienti) {
                Camera cam = camereRoma.get(0);
                createPrenotazioneTest(c, cam, LocalDate.now().minusDays(60), LocalDate.now().minusDays(55), StatoPrenotazione.CHECKED_OUT);
            }

            List<Camera> tutteLeCamere = new ArrayList<>();
            tutteLeCamere.addAll(camereCortina);
            tutteLeCamere.addAll(camereRoma);
            
            Random random = new Random();

            for (int i = 0; i < tutteLeCamere.size(); i++) {
                if (i % 2 == 0) {
                    Camera camera = tutteLeCamere.get(i);
                    
                    Cliente cliente = null;
                    for (Cliente c : clienti) {
                        if (clienteService.canBook(c.getId())) {
                            cliente = c;
                            break;
                        }
                    }
                    
                    if (cliente == null) continue;
                    
                    int scenario = random.nextInt(4);
                    LocalDate start, end;
                    StatoPrenotazione stato;
                    
                    switch (scenario) {
                        case 0:
                            start = LocalDate.now().minusDays(1);
                            end = LocalDate.now().plusDays(3);
                            stato = StatoPrenotazione.CHECKED_IN;
                            camera.setStatus(StatoCamera.OCCUPATA);
                            cameraRepository.save(camera);
                            break;
                        case 1:
                            start = LocalDate.now();
                            end = LocalDate.now().plusDays(2);
                            stato = StatoPrenotazione.CONFERMATA;
                            break;
                        case 2:
                            start = LocalDate.now().minusDays(3);
                            end = LocalDate.now();
                            stato = StatoPrenotazione.CHECKED_IN;
                            camera.setStatus(StatoCamera.OCCUPATA);
                            cameraRepository.save(camera);
                            break;
                        default:
                            start = LocalDate.now().plusDays(5);
                            end = LocalDate.now().plusDays(10);
                            stato = StatoPrenotazione.CONFERMATA;
                            break;
                    }

                    Prenotazione p = createPrenotazioneTest(cliente, camera, start, end, stato);
                    
                    if (cliente.getUtente().getUsername().equals("diego")) {
                        Ospite giulia = new Ospite();
                        giulia.setPrenotazione(p);
                        giulia.setNome("Giulia");
                        giulia.setCognome("Greco");
                        giulia.setCittadinanza("Italiana");
                        giulia.setLuogo("Palermo");
                        giulia.setDataNascita(LocalDate.of(2005, 6, 11));
                        ospiteRepository.save(giulia);
                        p.setNumeroOspiti(2);
                        prenotazioneRepository.save(p);
                    } else if (stato == StatoPrenotazione.CHECKED_IN || stato == StatoPrenotazione.CHECKED_OUT) {
                        int numOspitiExtra = random.nextInt(3);
                        p.setNumeroOspiti(1 + numOspitiExtra);
                        prenotazioneRepository.save(p);
                        
                        for (int k = 0; k < numOspitiExtra; k++) {
                            boolean isUnder12 = random.nextBoolean();
                            LocalDate dataNascitaOspite = isUnder12 ? LocalDate.now().minusYears(5) : LocalDate.now().minusYears(25);
                            
                            Ospite ospite = new Ospite();
                            ospite.setPrenotazione(p);
                            ospite.setNome("Ospite" + k);
                            ospite.setCognome("Extra");
                            ospite.setCittadinanza("Italiana");
                            ospite.setLuogo("Roma");
                            ospite.setDataNascita(dataNascitaOspite);
                            ospiteRepository.save(ospite);
                        }
                    }
                    
                    Collections.shuffle(clienti);
                }
            }

            System.out.println("Data Seeding completato con successo!");
            
            // Esegui scheduler per pulizia e aggiornamento stati
            System.out.println("Esecuzione scheduler post-seeding...");
            bookingCleanupScheduler.cancelNoShowBookings();
            roomScheduler.forceRoomCleanupStatus();
            System.out.println("Scheduler eseguiti.");

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

    private void createAdmin() {
        if (utenteRepository.findByUsername("admin").isEmpty()) {
            Utente adminUser = new Utente("admin", passwordEncoder.encode("admin"), "Admin", "Superuser");
            utenteRepository.save(adminUser);
            Dipendente adminDipendente = new Dipendente(Ruolo.AMMINISTRATORE, adminUser);
            dipendenteRepository.save(adminDipendente);
        }
    }

    private void createStaff(String username, String nome, String cognome, Sede sede) {
        if (utenteRepository.findByUsername(username).isEmpty()) {
            Utente staffUser = new Utente(username, passwordEncoder.encode("password"), nome, cognome);
            utenteRepository.save(staffUser);
            Dipendente staffDipendente = new Dipendente(Ruolo.STAFF, staffUser);
            staffDipendente.setSede(sede);
            dipendenteRepository.save(staffDipendente);
        }
    }

    private Cliente createCliente(String username, String nome, String cognome, String dataNascita) {
        if (utenteRepository.findByUsername(username).isPresent()) {
            Utente u = utenteRepository.findByUsername(username).get();
            return clienteRepository.findByUtenteId(u.getId()).orElseThrow();
        }
        
        Utente user = new Utente(username, passwordEncoder.encode("password"), nome, cognome);
        utenteRepository.save(user);
        Cliente cliente = new Cliente("Italiana", "Roma", dataNascita, "Carta d'Identità", "DOC12345", user);
        return clienteRepository.save(cliente);
    }
}
