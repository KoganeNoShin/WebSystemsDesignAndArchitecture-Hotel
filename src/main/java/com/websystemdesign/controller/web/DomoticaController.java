package com.websystemdesign.controller.web;

import com.websystemdesign.model.Camera;
import com.websystemdesign.model.Cliente;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.model.Utente;
import com.websystemdesign.service.CameraService;
import com.websystemdesign.service.ClienteService;
import com.websystemdesign.service.PrenotazioneService;
import com.websystemdesign.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/cliente/domotica")
public class DomoticaController {

    private final PrenotazioneService prenotazioneService;
    private final UtenteService utenteService;
    private final ClienteService clienteService;
    private final CameraService cameraService;

    @Autowired
    public DomoticaController(PrenotazioneService prenotazioneService, UtenteService utenteService, ClienteService clienteService, CameraService cameraService) {
        this.prenotazioneService = prenotazioneService;
        this.utenteService = utenteService;
        this.clienteService = clienteService;
        this.cameraService = cameraService;
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
            Object tempObj = payload.get("temperatura");
            if (tempObj instanceof Number) {
                camera.setTemperatura(((Number) tempObj).floatValue());
            }
        }

        cameraService.saveRoom(camera);
        return ResponseEntity.ok().body(Map.of("success", true));
    }

    private Prenotazione getActiveBooking(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteService.getUtenteByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteService.getClienteByUsername(utente.getUsername()).orElseThrow();

        return prenotazioneService.getPrenotazioniByCliente(cliente.getId()).stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_IN)
                .findFirst()
                .orElse(null);
    }
}
