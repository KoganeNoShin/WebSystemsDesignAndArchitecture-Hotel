package com.websystemdesign.controller.web;

import com.websystemdesign.model.Cliente;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.Utente;
import com.websystemdesign.repository.ClienteRepository;
import com.websystemdesign.repository.PrenotazioneRepository;
import com.websystemdesign.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        // Prenotazione Attiva (una sola)
        Optional<Prenotazione> attivaOpt = tutteLePrenotazioni.stream()
                .filter(p -> !p.getDataInizio().isAfter(oggi) && !p.getDataFine().isBefore(oggi))
                .findFirst();
        attivaOpt.ifPresent(p -> model.addAttribute("prenotazioneAttiva", p));

        // Prenotazioni Future
        List<Prenotazione> future = tutteLePrenotazioni.stream()
                .filter(p -> p.getDataInizio().isAfter(oggi))
                .collect(Collectors.toList());
        model.addAttribute("prenotazioniFuture", future);

        // Prenotazioni Passate
        List<Prenotazione> passate = tutteLePrenotazioni.stream()
                .filter(p -> p.getDataFine().isBefore(oggi))
                .collect(Collectors.toList());
        model.addAttribute("prenotazioniPassate", passate);

        return "cliente/dashboard";
    }
}
