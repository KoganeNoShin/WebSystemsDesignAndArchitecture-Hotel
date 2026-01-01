package com.websystemdesign.controller.web;

import com.websystemdesign.model.*;
import com.websystemdesign.repository.ClienteRepository;
import com.websystemdesign.repository.PrenotazioneRepository;
import com.websystemdesign.repository.UtenteRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente")
public class ClienteDashboardController {

    private final UtenteRepository utenteRepository;
    private final ClienteRepository clienteRepository;
    private final PrenotazioneRepository prenotazioneRepository;

    @Autowired
    public ClienteDashboardController(UtenteRepository utenteRepository, ClienteRepository clienteRepository, PrenotazioneRepository prenotazioneRepository) {
        this.utenteRepository = utenteRepository;
        this.clienteRepository = clienteRepository;
        this.prenotazioneRepository = prenotazioneRepository;
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
        LocalDate oggi = LocalDate.now();

        // Escludi cancellate per le sezioni attive/future
        List<Prenotazione> prenotazioniValide = tutteLePrenotazioni.stream()
                .filter(p -> p.getStato() != StatoPrenotazione.CANCELLATA)
                .collect(Collectors.toList());

        // Prenotazione Attiva (CHECKED_IN e nel range di date)
        Optional<Prenotazione> attivaOpt = prenotazioniValide.stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_IN)
                .filter(p -> !p.getDataInizio().isAfter(oggi) && !p.getDataFine().isBefore(oggi))
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
        }

        // Prenotazioni Future (Solo CONFERMATA)
        List<Prenotazione> future = prenotazioniValide.stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CONFERMATA)
                .filter(p -> p.getDataInizio().isAfter(oggi))
                .collect(Collectors.toList());
        model.addAttribute("prenotazioniFuture", future);

        // Prenotazioni Passate (CHECKED_OUT o data passata)
        List<Prenotazione> passate = tutteLePrenotazioni.stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_OUT || p.getDataFine().isBefore(oggi))
                .collect(Collectors.toList());
        model.addAttribute("prenotazioniPassate", passate);
        
        boolean hasFutureBooking = !future.isEmpty();
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
