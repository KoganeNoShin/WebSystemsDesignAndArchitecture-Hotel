package com.websystemdesign.controller.web;

import com.websystemdesign.model.*;
import com.websystemdesign.repository.ClienteRepository;
import com.websystemdesign.repository.PrenotazioneRepository;
import com.websystemdesign.repository.UtenteRepository;
import com.websystemdesign.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente")
public class ClienteDashboardController {

    private final UtenteRepository utenteRepository;
    private final ClienteRepository clienteRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final ClienteService clienteService;

    @Autowired
    public ClienteDashboardController(UtenteRepository utenteRepository, ClienteRepository clienteRepository, PrenotazioneRepository prenotazioneRepository, ClienteService clienteService) {
        this.utenteRepository = utenteRepository;
        this.clienteRepository = clienteRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.clienteService = clienteService;
    }

    @GetMapping("/dashboard")
    public String showClienteDashboard(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Utente non trovato"));
        Cliente cliente = clienteRepository.findByUtenteId(utente.getId())
                .orElseThrow(() -> new IllegalStateException("Cliente non trovato"));

        model.addAttribute("utente", utente);

        List<Prenotazione> tutteLePrenotazioni = prenotazioneRepository.findByClienteId(cliente.getId());
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        List<Prenotazione> prenotazioniValide = tutteLePrenotazioni.stream()
                .filter(p -> p.getStato() != StatoPrenotazione.CANCELLATA)
                .collect(Collectors.toList());

        Optional<Prenotazione> attivaOpt = prenotazioniValide.stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_IN)
                .filter(p -> p.getCamera().getStatus() == StatoCamera.OCCUPATA)
                .filter(p -> now.isAfter(p.getDataInizio().atTime(12, 0)) || now.isEqual(p.getDataInizio().atTime(12, 0)))
                .findFirst();
        
        if (attivaOpt.isPresent()) {
            Prenotazione attiva = attivaOpt.get();
            model.addAttribute("prenotazioneAttiva", attiva);
            
            double costoMultimedia = 0.0;
            if (attiva.getMultimedia() != null) {
                costoMultimedia = attiva.getMultimedia().stream().mapToDouble(Multimedia::getCosto).sum();
            }
            model.addAttribute("costoMultimedia", costoMultimedia);
            model.addAttribute("costoTotaleAttuale", attiva.getCosto());
            
            boolean isExpired = now.isAfter(attiva.getDataFine().atTime(11, 0));
            model.addAttribute("isExpired", isExpired);
            model.addAttribute("isRoomReady", true); 
        }

        List<Prenotazione> future = prenotazioniValide.stream()
                .filter(p -> {
                    boolean isConfermata = p.getStato() == StatoPrenotazione.CONFERMATA;
                    boolean isCheckedInNotActive = false;
                    if (p.getStato() == StatoPrenotazione.CHECKED_IN) {
                        if (attivaOpt.isPresent() && p.getId().equals(attivaOpt.get().getId())) {
                            return false;
                        }
                        isCheckedInNotActive = true;
                    }
                    boolean isFutureDate = p.getDataInizio().isAfter(today) || p.getDataInizio().isEqual(today);
                    return (isConfermata || isCheckedInNotActive) && isFutureDate;
                })
                .collect(Collectors.toList());
        model.addAttribute("prenotazioniFuture", future);

        List<Prenotazione> passate = tutteLePrenotazioni.stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_OUT)
                .collect(Collectors.toList());
        model.addAttribute("prenotazioniPassate", passate);
        
        boolean hasFutureBooking = !clienteService.canBook(cliente.getId());
        model.addAttribute("hasFutureBooking", hasFutureBooking);

        return "cliente/dashboard";
    }

    @PostMapping("/booking/cancel")
    public String cancelBooking(@RequestParam Long prenotazioneId, Authentication authentication, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteRepository.findByUtenteId(utente.getId()).orElseThrow();

        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        if (!prenotazione.getCliente().getId().equals(cliente.getId())) {
            throw new SecurityException("Non puoi cancellare una prenotazione non tua.");
        }

        if (prenotazione.getDataInizio().isBefore(LocalDate.now()) || prenotazione.getDataInizio().isEqual(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Non puoi cancellare una prenotazione già iniziata o passata.");
            return "redirect:/cliente/dashboard";
        }
        
        if (prenotazione.getStato() == StatoPrenotazione.CHECKED_IN || prenotazione.getStato() == StatoPrenotazione.CHECKED_OUT) {
             redirectAttributes.addFlashAttribute("errorMessage", "Non puoi cancellare una prenotazione con check-in già effettuato.");
             return "redirect:/cliente/dashboard";
        }

        prenotazione.setStato(StatoPrenotazione.CANCELLATA);
        prenotazioneRepository.save(prenotazione);

        redirectAttributes.addFlashAttribute("successMessage", "Prenotazione cancellata con successo.");
        return "redirect:/cliente/dashboard";
    }
}
