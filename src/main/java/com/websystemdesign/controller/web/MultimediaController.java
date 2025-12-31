package com.websystemdesign.controller.web;

import com.websystemdesign.model.Cliente;
import com.websystemdesign.model.Multimedia;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.model.Utente;
import com.websystemdesign.repository.ClienteRepository;
import com.websystemdesign.repository.MultimediaRepository;
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
import java.util.Set;

@Controller
@RequestMapping("/cliente/multimedia")
public class MultimediaController {

    private final MultimediaRepository multimediaRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public MultimediaController(MultimediaRepository multimediaRepository, PrenotazioneRepository prenotazioneRepository, UtenteRepository utenteRepository, ClienteRepository clienteRepository) {
        this.multimediaRepository = multimediaRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public String showMultimediaPage(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteRepository.findByUtenteId(utente.getId()).orElseThrow();

        // Trova la prenotazione attiva (CHECKED_IN)
        Optional<Prenotazione> prenotazioneOpt = prenotazioneRepository.findByClienteId(cliente.getId()).stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_IN)
                .findFirst();

        if (prenotazioneOpt.isEmpty()) {
            return "redirect:/cliente/dashboard"; // Nessun soggiorno attivo
        }

        Prenotazione prenotazione = prenotazioneOpt.get();
        List<Multimedia> catalogo = multimediaRepository.findAll();
        Set<Multimedia> acquistati = prenotazione.getMultimedia();

        // Rimuovi gli acquistati dal catalogo per non mostrarli doppi (opzionale, ma pulito)
        catalogo.removeAll(acquistati);

        model.addAttribute("prenotazione", prenotazione);
        model.addAttribute("catalogo", catalogo);
        model.addAttribute("acquistati", acquistati);

        return "cliente/multimedia";
    }

    @PostMapping("/buy")
    public String buyContent(@RequestParam Long multimediaId, @RequestParam Long prenotazioneId, RedirectAttributes redirectAttributes) {
        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId).orElseThrow();
        Multimedia contenuto = multimediaRepository.findById(multimediaId).orElseThrow();

        // Aggiungi contenuto
        if (prenotazione.getMultimedia() == null) {
            prenotazione.setMultimedia(new java.util.HashSet<>());
        }
        
        if (!prenotazione.getMultimedia().contains(contenuto)) {
            prenotazione.getMultimedia().add(contenuto);
            // Aggiorna costo totale prenotazione (aggiungendo costo contenuto)
            // Nota: Questo costo verrà saldato al check-out
            // Potremmo voler tenere traccia dei costi extra separatamente, ma per ora sommiamo al totale.
            // Oppure non sommiamo qui e calcoliamo il totale dinamico al check-out.
            // Sommiamo per semplicità di visualizzazione nella dashboard.
            prenotazione.setCosto(prenotazione.getCosto() + contenuto.getCosto());
            
            prenotazioneRepository.save(prenotazione);
            redirectAttributes.addFlashAttribute("successMessage", "Contenuto acquistato! Buona visione.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Hai già acquistato questo contenuto.");
        }

        return "redirect:/cliente/multimedia";
    }
}
