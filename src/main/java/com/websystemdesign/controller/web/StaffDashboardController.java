package com.websystemdesign.controller.web;

import com.websystemdesign.dto.StaffCameraDto;
import com.websystemdesign.model.*;
import com.websystemdesign.service.CameraService;
import com.websystemdesign.service.DipendenteService;
import com.websystemdesign.service.PrenotazioneService;
import com.websystemdesign.service.UtenteService;
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

    private final DipendenteService dipendenteService;
    private final UtenteService utenteService;
    private final CameraService cameraService;
    private final PrenotazioneService prenotazioneService;

    public StaffDashboardController(DipendenteService dipendenteService,
                                    UtenteService utenteService,
                                    CameraService cameraService,
                                    PrenotazioneService prenotazioneService) {
        this.dipendenteService = dipendenteService;
        this.utenteService = utenteService;
        this.cameraService = cameraService;
        this.prenotazioneService = prenotazioneService;
    }

    @GetMapping("/dashboard")
    public String showStaffDashboard(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        Utente utente = utenteService.getUtenteByUsername(currentUser.getUsername()).orElseThrow();

        Dipendente dipendente = dipendenteService.getDipendenteByUtenteId(utente.getId())
                .orElseThrow(() -> new RuntimeException("L'utente corrente non è registrato come dipendente"));

        Sede sede = dipendente.getSede();
        model.addAttribute("nomeSede", sede.getNome());

        List<Camera> camereDb = cameraService.getCamereBySede(sede.getId());
        List<StaffCameraDto> dashboardData = new ArrayList<>();

        LocalDate oggi = LocalDate.now();

        for (Camera c : camereDb) {
            StaffCameraDto dto = new StaffCameraDto();
            dto.setId(c.getId());
            dto.setNumero(c.getNumero());
            dto.setStatus(c.getStatus());
            dto.setNote(new ArrayList<>());

            Optional<Prenotazione> prenotazioneAttiva = prenotazioneService.getAllPrenotazioni().stream()
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
        Camera c = cameraService.getRoomById(id).orElseThrow();
        c.setStatus(StatoCamera.LIBERA);
        cameraService.saveRoom(c);
        return "redirect:/staff/dashboard";
    }
}
