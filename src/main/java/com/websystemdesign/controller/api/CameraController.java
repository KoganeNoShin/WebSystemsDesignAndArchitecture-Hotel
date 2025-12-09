package com.websystemdesign.controller.api;

import com.websystemdesign.model.Camera;
import com.websystemdesign.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// @RestController dice a Spring che qui gestiamo dati (JSON), non pagine HTML semplici
@RestController
@RequestMapping("/api/rooms") // L'indirizzo base per le stanze
public class CameraController {

    private final CameraService cameraService;

    // Colleghiamo lo Chef (Service) al Cameriere (Controller)
    @Autowired
    public CameraController(CameraService roomService) {
        this.cameraService = roomService;
    }

    // 1. Leggi tutte le stanze
    @GetMapping
    public List<Camera> getAllCamera() {
        return cameraService.getAllCamera();
    }

    // 2. Leggi una stanza specifica per ID
    @GetMapping("/{id}")
    public Optional<Camera> getRoomById(@PathVariable Long id) {
        return cameraService.getRoomById(id);
    }

    // 3. Crea una nuova stanza (Salva nel DB)
    @PostMapping
    public Camera createRoom(@RequestBody Camera camera) {
        return cameraService.saveRoom(camera);
    }

    // 4. Cancella una stanza
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable Long id) {
        cameraService.deleteRoom(id);
    }
}
