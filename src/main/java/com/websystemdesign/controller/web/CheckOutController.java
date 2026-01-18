package com.websystemdesign.controller.web;

import com.websystemdesign.model.*;
import com.websystemdesign.service.CameraService;
import com.websystemdesign.service.ClienteService;
import com.websystemdesign.service.PrenotazioneService;
import com.websystemdesign.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/cliente/checkout")
public class CheckOutController {

    private final PrenotazioneService prenotazioneService;
    private final UtenteService utenteService;
    private final ClienteService clienteService;
    private final CameraService cameraService;

    @Autowired
    public CheckOutController(PrenotazioneService prenotazioneService, UtenteService utenteService, ClienteService clienteService, CameraService cameraService) {
        this.prenotazioneService = prenotazioneService;
        this.utenteService = utenteService;
        this.clienteService = clienteService;
        this.cameraService = cameraService;
    }

    @GetMapping("/{id}")
    public String showCheckOutPage(@PathVariable Long id, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteService.getUtenteByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteService.getClienteByUsername(utente.getUsername()).orElseThrow();

        Prenotazione prenotazione = prenotazioneService.getPrenotazioneById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        if (!prenotazione.getCliente().getId().equals(cliente.getId())) {
            return "redirect:/cliente/dashboard";
        }

        if (prenotazione.getStato() != StatoPrenotazione.CHECKED_IN) {
            redirectAttributes.addFlashAttribute("errorMessage", "Il check-out è possibile solo per soggiorni in corso.");
            return "redirect:/cliente/dashboard";
        }

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
            sconto = costoNottiRimanenti * 0.5f;
        }
        
        float costoCameraFinale = costoNottiUsufruite + (costoNottiRimanenti - sconto);
        
        float costoServizi = (float) prenotazione.getServices().stream().mapToDouble(Service::getCosto).sum();
        float costoMultimedia = (float) prenotazione.getMultimedia().stream().mapToDouble(Multimedia::getCosto).sum();
        
        float tassaSoggiorno = 0.0f;
        try {
            tassaSoggiorno = Float.parseFloat(prenotazione.getCamera().getSede().getTassaSoggiorno());
        } catch (NumberFormatException e) {
            // ignore
        }
        
        int numOspitiPaganti = 1;
        int numEsenzioni = 0;

        // Check ospiti
        if (prenotazione.getOspiti() != null) {
            for (Ospite o : prenotazione.getOspiti()) {
                if (isUnder12(o.getDataNascita())) {
                    numEsenzioni++;
                } else {
                    numOspitiPaganti++;
                }
            }
        }
        
        float costoTassaSoggiorno = tassaSoggiorno * numOspitiPaganti * nottiUsufruite;
        
        float totaleDaPagare = costoCameraFinale + costoServizi + costoMultimedia + costoTassaSoggiorno;

        model.addAttribute("prenotazione", prenotazione);
        model.addAttribute("isAnticipato", isAnticipato);
        model.addAttribute("nottiUsufruite", nottiUsufruite);
        model.addAttribute("nottiRimanenti", nottiRimanenti);
        model.addAttribute("sconto", sconto);
        model.addAttribute("costoCameraFinale", costoCameraFinale);
        model.addAttribute("costoServizi", costoServizi);
        model.addAttribute("costoMultimedia", costoMultimedia);
        model.addAttribute("costoTassaSoggiorno", costoTassaSoggiorno);
        model.addAttribute("numEsenzioni", numEsenzioni);
        model.addAttribute("totaleDaPagare", totaleDaPagare);

        return "cliente/checkout";
    }

    @PostMapping("/confirm")
    public String processCheckOut(@RequestParam Long prenotazioneId, 
                                  @RequestParam float totalePagato,
                                  Authentication authentication, 
                                  RedirectAttributes redirectAttributes) {
        
        Prenotazione prenotazione = prenotazioneService.getPrenotazioneById(prenotazioneId).orElseThrow();
        
        prenotazione.setStato(StatoPrenotazione.CHECKED_OUT);
        prenotazione.setCosto(totalePagato);
        if (LocalDate.now().isBefore(prenotazione.getDataFine())) {
            prenotazione.setDataFine(LocalDate.now());
        }
        prenotazioneService.savePrenotazione(prenotazione);
        
        Camera camera = prenotazione.getCamera();
        camera.setStatus(StatoCamera.DA_PULIRE);
        cameraService.saveRoom(camera);

        redirectAttributes.addFlashAttribute("successMessage", "Check-out completato. Grazie per aver soggiornato da noi!");
        return "redirect:/cliente/dashboard";
    }

    private boolean isUnder12(LocalDate dataNascita) {
        if (dataNascita == null) return false;
        return Period.between(dataNascita, LocalDate.now()).getYears() < 12;
    }
}
