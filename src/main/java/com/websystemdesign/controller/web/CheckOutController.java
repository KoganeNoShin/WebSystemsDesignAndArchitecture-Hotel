package com.websystemdesign.controller.web;

import com.websystemdesign.model.*;
import com.websystemdesign.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/cliente/checkout")
public class CheckOutController {

    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final ClienteRepository clienteRepository;
    private final CameraRepository cameraRepository;

    @Autowired
    public CheckOutController(PrenotazioneRepository prenotazioneRepository, UtenteRepository utenteRepository, ClienteRepository clienteRepository, CameraRepository cameraRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.clienteRepository = clienteRepository;
        this.cameraRepository = cameraRepository;
    }

    @GetMapping("/{id}")
    public String showCheckOutPage(@PathVariable Long id, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteRepository.findByUtenteId(utente.getId()).orElseThrow();

        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        if (!prenotazione.getCliente().getId().equals(cliente.getId())) {
            return "redirect:/cliente/dashboard";
        }

        if (prenotazione.getStato() != StatoPrenotazione.CHECKED_IN) {
            redirectAttributes.addFlashAttribute("errorMessage", "Il check-out è possibile solo per soggiorni in corso.");
            return "redirect:/cliente/dashboard";
        }

        // Calcolo Costi e Sconti
        LocalDate oggi = LocalDate.now();
        LocalDate checkin = prenotazione.getDataInizio();
        LocalDate checkoutPrevisto = prenotazione.getDataFine();
        
        long nottiTotaliPreviste = ChronoUnit.DAYS.between(checkin, checkoutPrevisto);
        if (nottiTotaliPreviste < 1) nottiTotaliPreviste = 1;
        
        long nottiUsufruite = ChronoUnit.DAYS.between(checkin, oggi);
        if (nottiUsufruite < 0) nottiUsufruite = 0; 
        
        long nottiRimanenti = nottiTotaliPreviste - nottiUsufruite;
        if (nottiRimanenti < 0) nottiRimanenti = 0;

        float prezzoNotte = prenotazione.getCamera().getPrezzoBase();
        float costoNottiUsufruite = nottiUsufruite * prezzoNotte;
        float costoNottiRimanenti = nottiRimanenti * prezzoNotte;
        float sconto = 0.0f;
        
        boolean isAnticipato = nottiRimanenti > 0;
        
        if (isAnticipato) {
            sconto = costoNottiRimanenti * 0.5f; // Sconto 50% sulle notti non godute
        }
        
        float costoCameraFinale = costoNottiUsufruite + (costoNottiRimanenti - sconto);
        
        // Costi Extra
        float costoServizi = (float) prenotazione.getServices().stream().mapToDouble(Service::getCosto).sum();
        float costoMultimedia = (float) prenotazione.getMultimedia().stream().mapToDouble(Multimedia::getCosto).sum();
        
        float totaleDaPagare = costoCameraFinale + costoServizi + costoMultimedia;

        model.addAttribute("prenotazione", prenotazione);
        model.addAttribute("isAnticipato", isAnticipato);
        model.addAttribute("nottiUsufruite", nottiUsufruite);
        model.addAttribute("nottiRimanenti", nottiRimanenti);
        model.addAttribute("sconto", sconto);
        model.addAttribute("costoCameraFinale", costoCameraFinale);
        model.addAttribute("costoServizi", costoServizi);
        model.addAttribute("costoMultimedia", costoMultimedia);
        model.addAttribute("totaleDaPagare", totaleDaPagare);

        return "cliente/checkout";
    }

    @PostMapping("/confirm")
    public String processCheckOut(@RequestParam Long prenotazioneId, 
                                  @RequestParam float totalePagato,
                                  Authentication authentication, 
                                  RedirectAttributes redirectAttributes) {
        
        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId).orElseThrow();
        
        // Aggiorna stato prenotazione
        prenotazione.setStato(StatoPrenotazione.CHECKED_OUT);
        prenotazione.setCosto(totalePagato);
        if (LocalDate.now().isBefore(prenotazione.getDataFine())) {
            prenotazione.setDataFine(LocalDate.now());
        }
        prenotazioneRepository.save(prenotazione);
        
        // Aggiorna stato camera
        Camera camera = prenotazione.getCamera();
        camera.setStatus(StatoCamera.DA_PULIRE); // Corretto da IN_PULIZIA a DA_PULIRE
        cameraRepository.save(camera);

        redirectAttributes.addFlashAttribute("successMessage", "Check-out completato. Grazie per aver soggiornato da noi!");
        return "redirect:/cliente/dashboard";
    }
}
