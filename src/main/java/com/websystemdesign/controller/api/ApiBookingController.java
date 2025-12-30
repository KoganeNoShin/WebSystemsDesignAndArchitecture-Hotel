package com.websystemdesign.controller.api;

import com.websystemdesign.dto.CameraDto;
import com.websystemdesign.model.Camera;
import com.websystemdesign.repository.PrenotazioneRepository;
import com.websystemdesign.service.CameraService;
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
    private final PrenotazioneRepository prenotazioneRepository;

    @Autowired
    public ApiBookingController(CameraService cameraService, PrenotazioneRepository prenotazioneRepository) {
        this.cameraService = cameraService;
        this.prenotazioneRepository = prenotazioneRepository;
    }

    @GetMapping("/available-rooms")
    public List<CameraDto> getAvailableRooms(
            @RequestParam Long sedeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkout,
            @RequestParam int numOspiti) {

        // 1. Trova tutte le camere della sede
        List<Camera> camereSede = cameraService.getCamereBySede(sedeId);

        // 2. Trova gli ID delle camere già prenotate in quelle date
        List<Long> idCamereOccupate = prenotazioneRepository.findAll().stream()
                .filter(p -> p.getCamera().getSede().getId().equals(sedeId))
                .filter(p -> p.getDataInizio().isBefore(checkout) && p.getDataFine().isAfter(checkin))
                .map(p -> p.getCamera().getId())
                .collect(Collectors.toList());

        // 3. Filtra le camere disponibili e con capienza sufficiente
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
        
        // Popolamento campi domotica
        dto.setLuce(camera.isLuce());
        dto.setTapparelle(camera.isTapparelle());
        dto.setTemperatura(camera.getTemperatura());

        return dto;
    }
}
