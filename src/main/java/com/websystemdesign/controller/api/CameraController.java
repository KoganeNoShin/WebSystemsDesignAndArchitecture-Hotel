package com.websystemdesign.controller.api;

import com.websystemdesign.dto.CameraDto;
import com.websystemdesign.mapper.CameraMapper;
import com.websystemdesign.model.Camera;
import com.websystemdesign.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class CameraController {

    private final CameraService cameraService;
    private final CameraMapper cameraMapper;

    @Autowired
    public CameraController(CameraService cameraService, CameraMapper cameraMapper) {
        this.cameraService = cameraService;
        this.cameraMapper = cameraMapper;
    }

    // 1. Leggi tutte le stanze (Restituisce DTO)
    @GetMapping
    public List<CameraDto> getAllCamera() {
        return cameraService.getAllCamera().stream()
                .map(cameraMapper::toDto)
                .collect(Collectors.toList());
    }

    // 2. Leggi una stanza specifica per ID (Restituisce DTO)
    @GetMapping("/{id}")
    public CameraDto getRoomById(@PathVariable Long id) {
        return cameraService.getRoomById(id)
                .map(cameraMapper::toDto)
                .orElse(null); // In un'app reale, qui restituiremmo un 404 Not Found
    }

    // 3. Crea una nuova stanza (Accetta DTO, Restituisce DTO)
    @PostMapping
    public CameraDto createRoom(@RequestBody CameraDto cameraDto) {
        // Convertiamo DTO -> Entity
        Camera camera = cameraMapper.toEntity(cameraDto);
        
        // Salviamo usando il service
        Camera savedCamera = cameraService.saveRoom(camera);
        
        // Convertiamo Entity salvata -> DTO per la risposta
        return cameraMapper.toDto(savedCamera);
    }

    // 4. Cancella una stanza
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Long id) {
        cameraService.deleteRoom(id);
    }
}
