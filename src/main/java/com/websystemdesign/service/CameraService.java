package com.websystemdesign.service;

import com.websystemdesign.model.Camera;
import com.websystemdesign.repository.CameraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CameraService {
    private final CameraRepository cameraRepository;

    @Autowired
    public CameraService(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    // Metodo per ottenere TUTTE le stanze (Menu)
    public List<Camera> getAllCamera() {
        return cameraRepository.findAll();
    }

    // Metodo per cercare una stanza specifica per ID
    public Optional<Camera> getRoomById(Long id) {
        return cameraRepository.findById(id);
    }

    // Metodo per salvare o aggiornare una stanza
    public Camera saveRoom(Camera room) {
        return cameraRepository.save(room); // Il repository fa tutto da solo!
    }

    // Metodo per cancellare una stanza
    public void deleteRoom(Long id) {
        cameraRepository.deleteById(id);
    }
}
