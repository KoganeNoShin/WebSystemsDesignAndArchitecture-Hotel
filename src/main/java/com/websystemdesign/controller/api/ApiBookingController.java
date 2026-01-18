package com.websystemdesign.controller.api;

import com.websystemdesign.dto.CameraDto;
import com.websystemdesign.model.Camera;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.service.CameraService;
import com.websystemdesign.service.PrenotazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/booking")
public class ApiBookingController {

    private final CameraService cameraService;
    private final PrenotazioneService prenotazioneService;

    @Autowired
    public ApiBookingController(CameraService cameraService, PrenotazioneService prenotazioneService) {
        this.cameraService = cameraService;
        this.prenotazioneService = prenotazioneService;
    }

    @GetMapping("/available-rooms")
    public List<CameraDto> getAvailableRooms(
            @RequestParam Long sedeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkout,
            @RequestParam int numOspiti) {

        List<Camera> camereSede = cameraService.getCamereBySede(sedeId);

        List<Long> idCamereOccupate = prenotazioneService.getAllPrenotazioni().stream()
                .filter(p -> p.getCamera().getSede().getId().equals(sedeId))
                .filter(p -> p.getStato() != StatoPrenotazione.CANCELLATA)
                .filter(p -> p.getDataInizio().isBefore(checkout) && p.getDataFine().isAfter(checkin))
                .map(p -> p.getCamera().getId())
                .collect(Collectors.toList());

        return camereSede.stream()
                .filter(camera -> !idCamereOccupate.contains(camera.getId()))
                .filter(camera -> camera.getPostiLetto() >= numOspiti)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CameraDto convertToDto(Camera camera) {
        CameraDto dto = new CameraDto();
        dto.setId(camera.getId());
        dto.setNumero(camera.getNumero());
        dto.setTipologia(camera.getTipologia());
        dto.setPostiLetto(camera.getPostiLetto());
        dto.setPrezzoBase(camera.getPrezzoBase());
        dto.setImmagini(cameraService.getImmaginiCamera(camera));
        dto.setSedeId(camera.getSede().getId());
        
        dto.setLuce(camera.isLuce());
        dto.setTapparelle(camera.isTapparelle());
        dto.setTemperatura(camera.getTemperatura());

        return dto;
    }
}
