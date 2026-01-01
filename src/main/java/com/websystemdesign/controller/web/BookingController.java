package com.websystemdesign.controller.web;

import com.websystemdesign.model.*;
import com.websystemdesign.repository.*;
import com.websystemdesign.service.CameraService;
import com.websystemdesign.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final SedeService sedeService;
    private final CameraService cameraService;
    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final ClienteRepository clienteRepository;
    private final ServiceRepository serviceRepository;

    @Autowired
    public BookingController(SedeService sedeService, CameraService cameraService, PrenotazioneRepository prenotazioneRepository, UtenteRepository utenteRepository, ClienteRepository clienteRepository, ServiceRepository serviceRepository) {
        this.sedeService = sedeService;
        this.cameraService = cameraService;
        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.clienteRepository = clienteRepository;
        this.serviceRepository = serviceRepository;
    }

    // Step 1: Scelta della Sede
    @GetMapping("/new")
    public String selectSede(Model model) {
        model.addAttribute("sedi", sedeService.getAllSedi());
        return "booking/select-sede";
    }

    // Step 2: Scelta della Camera (Mostra tutte le camere della sede)
    @GetMapping("/rooms")
    public String selectRoom(@RequestParam Long sedeId, Model model) {
        Sede sede = sedeService.getSedeById(sedeId).orElseThrow();
        List<Camera> camere = cameraService.getCamereBySede(sedeId);
        
        model.addAttribute("sede", sede);
        model.addAttribute("camere", camere);
        return "booking/select-room-step2";
    }

    // Step 3: Date, Ospiti e Servizi (per una camera specifica)
    @GetMapping("/dates")
    public String selectDates(@RequestParam Long cameraId, Model model) {
        Camera camera = cameraService.getRoomById(cameraId).orElseThrow();
        Sede sede = camera.getSede();

        model.addAttribute("camera", camera);
        model.addAttribute("sede", sede);
        model.addAttribute("serviziDisponibili", sede.getServices());
        
        return "booking/select-dates-step3";
    }

    // Step 4: Riepilogo
    @GetMapping("/review")
    public String reviewBooking(@RequestParam Long cameraId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkin,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkout,
                                @RequestParam int numOspiti,
                                @RequestParam(required = false) List<Long> selectedServices,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        // Validazione Date
        if (checkin.isBefore(LocalDate.now().plusDays(1))) {
            redirectAttributes.addFlashAttribute("errorMessage", "La prenotazione deve iniziare almeno da domani.");
            return "redirect:/booking/dates?cameraId=" + cameraId;
        }
        if (checkout.isBefore(checkin.plusDays(1))) {
            redirectAttributes.addFlashAttribute("errorMessage", "La data di check-out deve essere successiva al check-in.");
            return "redirect:/booking/dates?cameraId=" + cameraId;
        }

        Camera camera = cameraService.getRoomById(cameraId).orElseThrow();
        long nights = ChronoUnit.DAYS.between(checkin, checkout);
        if (nights < 1) nights = 1;

        Set<Service> servizi = new HashSet<>();
        if (selectedServices != null && !selectedServices.isEmpty()) {
            servizi.addAll(serviceRepository.findAllById(selectedServices));
        }

        float costoCamera = camera.getPrezzoBase() * nights;
        float costoServizi = (float) servizi.stream().mapToDouble(Service::getCosto).sum();
        float costoTotale = costoCamera + costoServizi;

        model.addAttribute("camera", camera);
        model.addAttribute("checkin", checkin);
        model.addAttribute("checkout", checkout);
        model.addAttribute("numOspiti", numOspiti);
        model.addAttribute("serviziSelezionati", servizi);
        model.addAttribute("costoCamera", costoCamera);
        model.addAttribute("costoServizi", costoServizi);
        model.addAttribute("costoTotale", costoTotale);
        model.addAttribute("nights", nights);

        return "booking/review";
    }

    // Step 5: Conferma e Salvataggio
    @PostMapping("/confirm")
    public String confirmBooking(@RequestParam Long cameraId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkin,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkout,
                                 @RequestParam int numOspiti,
                                 @RequestParam(required = false) List<Long> selectedServices,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        // Validazione Date (anche qui per sicurezza)
        if (checkin.isBefore(LocalDate.now().plusDays(1))) {
            redirectAttributes.addFlashAttribute("errorMessage", "La prenotazione deve iniziare almeno da domani.");
            return "redirect:/booking/dates?cameraId=" + cameraId;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteRepository.findByUtenteId(utente.getId()).orElseThrow();

        long nights = ChronoUnit.DAYS.between(checkin, checkout);
        if (nights < 1) nights = 1;

        Set<Service> servizi = new HashSet<>();
        if (selectedServices != null && !selectedServices.isEmpty()) {
            servizi.addAll(serviceRepository.findAllById(selectedServices));
        }

        Camera camera = cameraService.getRoomById(cameraId).orElseThrow();
        
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setCliente(cliente);
        prenotazione.setCamera(camera);
        prenotazione.setDataInizio(checkin);
        prenotazione.setDataFine(checkout);
        prenotazione.setNumeroOspiti(numOspiti);
        prenotazione.setStato(StatoPrenotazione.CONFERMATA);
        
        float costoServizi = (float) servizi.stream().mapToDouble(Service::getCosto).sum();
        float costoTotale = (camera.getPrezzoBase() * nights) + costoServizi;
        
        prenotazione.setCosto(costoTotale);
        prenotazione.setServices(new HashSet<>(servizi));

        prenotazioneRepository.save(prenotazione);

        redirectAttributes.addFlashAttribute("successMessage", "Prenotazione confermata con successo!");
        return "redirect:/cliente/dashboard";
    }
}
