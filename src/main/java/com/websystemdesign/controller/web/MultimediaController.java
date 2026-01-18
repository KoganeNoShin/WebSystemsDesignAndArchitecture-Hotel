package com.websystemdesign.controller.web;

import com.websystemdesign.model.Cliente;
import com.websystemdesign.model.Multimedia;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.model.Utente;
import com.websystemdesign.service.ClienteService;
import com.websystemdesign.service.MultimediaService;
import com.websystemdesign.service.PrenotazioneService;
import com.websystemdesign.service.UtenteService;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/cliente/multimedia")
public class MultimediaController {

    private final MultimediaService multimediaService;
    private final PrenotazioneService prenotazioneService;
    private final UtenteService utenteService;
    private final ClienteService clienteService;

    @Autowired
    public MultimediaController(MultimediaService multimediaService, PrenotazioneService prenotazioneService, UtenteService utenteService, ClienteService clienteService) {
        this.multimediaService = multimediaService;
        this.prenotazioneService = prenotazioneService;
        this.utenteService = utenteService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String showMultimediaPage(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteService.getUtenteByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteService.getClienteByUsername(utente.getUsername()).orElseThrow();

        Optional<Prenotazione> prenotazioneOpt = prenotazioneService.getPrenotazioniByCliente(cliente.getId()).stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_IN)
                .findFirst();

        if (prenotazioneOpt.isEmpty()) {
            return "redirect:/cliente/dashboard";
        }

        Prenotazione prenotazione = prenotazioneOpt.get();
        List<Multimedia> catalogo = multimediaService.getAllMultimedia();
        Set<Multimedia> acquistati = prenotazione.getMultimedia();

        catalogo.removeAll(acquistati);

        model.addAttribute("prenotazione", prenotazione);
        model.addAttribute("catalogo", catalogo);
        model.addAttribute("acquistati", acquistati);

        return "cliente/multimedia";
    }

    @PostMapping("/buy")
    public String buyContent(@RequestParam Long multimediaId, @RequestParam Long prenotazioneId, RedirectAttributes redirectAttributes) {
        Prenotazione prenotazione = prenotazioneService.getPrenotazioneById(prenotazioneId).orElseThrow();
        Multimedia contenuto = multimediaService.getMultimediaById(multimediaId).orElseThrow();

        if (prenotazione.getMultimedia() == null) {
            prenotazione.setMultimedia(new java.util.HashSet<>());
        }
        
        if (!prenotazione.getMultimedia().contains(contenuto)) {
            prenotazione.getMultimedia().add(contenuto);
            prenotazione.setCosto(prenotazione.getCosto() + contenuto.getCosto());
            
            prenotazioneService.savePrenotazione(prenotazione);
            redirectAttributes.addFlashAttribute("successMessage", "Contenuto acquistato! Buona visione.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Hai già acquistato questo contenuto.");
        }

        return "redirect:/cliente/multimedia";
    }
}
