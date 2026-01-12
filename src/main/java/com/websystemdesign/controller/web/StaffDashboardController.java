package com.websystemdesign.controller.web;

import com.websystemdesign.dto.StaffCameraDto;
import com.websystemdesign.model.*;
import com.websystemdesign.repository.CameraRepository;
import com.websystemdesign.repository.DipendenteRepository;
import com.websystemdesign.repository.PrenotazioneRepository;
import com.websystemdesign.repository.UtenteRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    private final DipendenteRepository dipendenteRepository;
    private final UtenteRepository utenteRepository;
    private final CameraRepository cameraRepository;
    private final PrenotazioneRepository prenotazioneRepository;

    public StaffDashboardController(DipendenteRepository dipendenteRepository,
                                    UtenteRepository utenteRepository,
                                    CameraRepository cameraRepository,
                                    PrenotazioneRepository prenotazioneRepository) {
        this.dipendenteRepository = dipendenteRepository;
        this.utenteRepository = utenteRepository;
        this.cameraRepository = cameraRepository;
        this.prenotazioneRepository = prenotazioneRepository;
    }

    @GetMapping("/dashboard")
    public String showStaffDashboard(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        Utente utente = utenteRepository.findByUsername(currentUser.getUsername()).orElseThrow();

        Dipendente dipendente = dipendenteRepository.findByUtenteId(utente.getId())
                .orElseThrow(() -> new RuntimeException("L'utente corrente non è registrato come dipendente"));

        Sede sede = dipendente.getSede();
        model.addAttribute("nomeSede", sede.getNome());

        List<Camera> camereDb = cameraRepository.findBySedeId(sede.getId());
        List<StaffCameraDto> dashboardData = new ArrayList<>();

        LocalDate oggi = LocalDate.now();

        for (Camera c : camereDb) {
            StaffCameraDto dto = new StaffCameraDto();
            dto.setId(c.getId());
            dto.setNumero(c.getNumero());
            dto.setStatus(c.getStatus());
            dto.setNote(new ArrayList<>());

            Optional<Prenotazione> prenotazioneAttiva = prenotazioneRepository.findAll().stream()
                    .filter(p -> p.getCamera().getId().equals(c.getId()))
                    .filter(p -> p.getStato() == StatoPrenotazione.CHECKED_IN)
                    .filter(p -> !p.getDataInizio().isAfter(oggi) && !p.getDataFine().isBefore(oggi))
                    .findFirst();

            if (prenotazioneAttiva.isPresent()) {
                Prenotazione p = prenotazioneAttiva.get();
                dto.setClienteAttuale(p.getCliente().getUtente().getCognome() + " " + p.getCliente().getUtente().getNome());

                for(Nota n : p.getNote()) {
                    dto.getNote().add(n.getTesto());
                }
            }

            dashboardData.add(dto);
        }

        model.addAttribute("camere", dashboardData);
        return "staff/dashboard";
    }

    @PostMapping("/camera/{id}/pulita")
    public String markAsClean(@PathVariable Long id) {
        Camera c = cameraRepository.findById(id).orElseThrow();
        c.setStatus(StatoCamera.LIBERA);
        cameraRepository.save(c);
        return "redirect:/staff/dashboard";
    }
}
