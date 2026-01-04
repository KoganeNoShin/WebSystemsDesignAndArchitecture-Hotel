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
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cliente/checkin")
public class CheckInController {

    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final ClienteRepository clienteRepository;
    private final OspiteRepository ospiteRepository;
    private final CameraRepository cameraRepository;

    @Autowired
    public CheckInController(PrenotazioneRepository prenotazioneRepository, UtenteRepository utenteRepository, ClienteRepository clienteRepository, OspiteRepository ospiteRepository, CameraRepository cameraRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.clienteRepository = clienteRepository;
        this.ospiteRepository = ospiteRepository;
        this.cameraRepository = cameraRepository;
    }

    @GetMapping("/{id}")
    public String showCheckInForm(@PathVariable Long id, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteRepository.findByUtenteId(utente.getId()).orElseThrow();

        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        if (!prenotazione.getCliente().getId().equals(cliente.getId())) {
            return "redirect:/cliente/dashboard";
        }

        // Nuova Regola: Check-in possibile fino alle 11:00 del giorno di arrivo
        LocalDateTime deadline = prenotazione.getDataInizio().atTime(11, 0);
        if (LocalDateTime.now().isAfter(deadline)) {
             redirectAttributes.addFlashAttribute("errorMessage", "Tempo scaduto per il Check-in online (limite ore 11:00 del giorno di arrivo).");
             return "redirect:/cliente/dashboard";
        }
        
        if (prenotazione.getStato() == StatoPrenotazione.CHECKED_IN) {
             redirectAttributes.addFlashAttribute("errorMessage", "Check-in già effettuato.");
             return "redirect:/cliente/dashboard";
        }

        int numOspitiExtra = prenotazione.getNumeroOspiti() - 1;
        if (numOspitiExtra < 0) numOspitiExtra = 0;

        List<Ospite> ospitiList = new ArrayList<>();
        for (int i = 0; i < numOspitiExtra; i++) {
            ospitiList.add(new Ospite());
        }
        
        boolean isProfileLocked = cliente.getCittadinanza() != null && !cliente.getCittadinanza().isEmpty() &&
                                  cliente.getLuogo() != null && !cliente.getLuogo().isEmpty() &&
                                  cliente.getDataNascita() != null && !cliente.getDataNascita().isEmpty();

        model.addAttribute("prenotazione", prenotazione);
        model.addAttribute("cliente", cliente);
        model.addAttribute("ospitiList", ospitiList);
        model.addAttribute("isProfileLocked", isProfileLocked);
        
        return "cliente/checkin";
    }

    @PostMapping("/confirm")
    public String processCheckIn(@RequestParam Long prenotazioneId,
                                 @ModelAttribute("cliente") Cliente clienteForm,
                                 @RequestParam(value = "ospitiNomi", required = false) List<String> nomi,
                                 @RequestParam(value = "ospitiCognomi", required = false) List<String> cognomi,
                                 @RequestParam(value = "ospitiCittadinanze", required = false) List<String> cittadinanze,
                                 @RequestParam(value = "ospitiLuoghi", required = false) List<String> luoghi,
                                 @RequestParam(value = "ospitiDate", required = false) List<String> date,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId).orElseThrow();
        Cliente clienteDb = prenotazione.getCliente();

        boolean wasProfileLocked = clienteDb.getCittadinanza() != null && !clienteDb.getCittadinanza().isEmpty();
        
        if (!wasProfileLocked) {
            clienteDb.setCittadinanza(clienteForm.getCittadinanza());
            clienteDb.setLuogo(clienteForm.getLuogo());
            clienteDb.setDataNascita(clienteForm.getDataNascita());
            
            if (clienteForm.getDataNascita() != null && !clienteForm.getDataNascita().isEmpty()) {
                if (Period.between(LocalDate.parse(clienteForm.getDataNascita()), LocalDate.now()).getYears() < 18) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Il capogruppo deve essere maggiorenne.");
                    return "redirect:/cliente/checkin/" + prenotazioneId;
                }
            }
        }
        
        clienteDb.setTipoDocumento(clienteForm.getTipoDocumento());
        clienteDb.setNumDocumento(clienteForm.getNumDocumento());
        clienteRepository.save(clienteDb);

        Ospite capogruppoOspite = new Ospite();
        capogruppoOspite.setPrenotazione(prenotazione);
        capogruppoOspite.setNome(clienteDb.getUtente().getNome());
        capogruppoOspite.setCognome(clienteDb.getUtente().getCognome());
        capogruppoOspite.setCittadinanza(clienteDb.getCittadinanza());
        capogruppoOspite.setLuogo(clienteDb.getLuogo());
        if (clienteDb.getDataNascita() != null) {
            capogruppoOspite.setDataNascita(LocalDate.parse(clienteDb.getDataNascita()));
        }
        ospiteRepository.save(capogruppoOspite);

        if (nomi != null) {
            for (int i = 0; i < nomi.size(); i++) {
                if (!nomi.get(i).isEmpty()) {
                    Ospite ospite = new Ospite();
                    ospite.setPrenotazione(prenotazione);
                    ospite.setNome(nomi.get(i));
                    ospite.setCognome(cognomi.get(i));
                    ospite.setCittadinanza(cittadinanze.get(i));
                    ospite.setLuogo(luoghi.get(i));
                    ospite.setDataNascita(LocalDate.parse(date.get(i)));
                    ospiteRepository.save(ospite);
                }
            }
        }

        // Aggiorna stato Prenotazione a CHECKED_IN
        prenotazione.setStato(StatoPrenotazione.CHECKED_IN);
        prenotazioneRepository.save(prenotazione);
        
        // NESSUN aggiornamento stato camera (gestito dal collega)

        redirectAttributes.addFlashAttribute("successMessage", "Check-in online completato con successo! Benvenuto.");
        return "redirect:/cliente/dashboard";
    }
}
