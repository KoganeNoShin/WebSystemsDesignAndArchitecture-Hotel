package com.websystemdesign.controller.web;

import com.websystemdesign.model.Camera;
import com.websystemdesign.model.Cliente;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.model.Utente;
import com.websystemdesign.repository.CameraRepository;
import com.websystemdesign.repository.ClienteRepository;
import com.websystemdesign.repository.PrenotazioneRepository;
import com.websystemdesign.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cliente/domotica")
public class DomoticaController {

    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final ClienteRepository clienteRepository;
    private final CameraRepository cameraRepository;

    @Autowired
    public DomoticaController(PrenotazioneRepository prenotazioneRepository, UtenteRepository utenteRepository, ClienteRepository clienteRepository, CameraRepository cameraRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.clienteRepository = clienteRepository;
        this.cameraRepository = cameraRepository;
    }

    @GetMapping
    public String showDomoticaPage(Model model, Authentication authentication) {
        Prenotazione prenotazione = getActiveBooking(authentication);
        if (prenotazione == null) {
            return "redirect:/cliente/dashboard";
        }

        model.addAttribute("camera", prenotazione.getCamera());
        return "cliente/domotica";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateDomotica(@RequestBody Map<String, Object> payload, Authentication authentication) {
        Prenotazione prenotazione = getActiveBooking(authentication);
        if (prenotazione == null) {
            return ResponseEntity.status(403).body("Nessuna prenotazione attiva");
        }

        Camera camera = prenotazione.getCamera();

        if (payload.containsKey("luce")) {
            camera.setLuce((Boolean) payload.get("luce"));
        }
        if (payload.containsKey("tapparelle")) {
            camera.setTapparelle((Boolean) payload.get("tapparelle"));
        }
        if (payload.containsKey("temperatura")) {
            // Gestione sicura del tipo numerico (Integer o Double)
            Object tempObj = payload.get("temperatura");
            if (tempObj instanceof Number) {
                camera.setTemperatura(((Number) tempObj).floatValue());
            }
        }

        cameraRepository.save(camera);
        return ResponseEntity.ok().body(Map.of("success", true));
    }

    private Prenotazione getActiveBooking(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteRepository.findByUtenteId(utente.getId()).orElseThrow();

        return prenotazioneRepository.findByClienteId(cliente.getId()).stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_IN)
                .findFirst()
                .orElse(null);
    }
}
