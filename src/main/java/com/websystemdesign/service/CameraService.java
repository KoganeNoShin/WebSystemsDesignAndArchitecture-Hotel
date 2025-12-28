package com.websystemdesign.service;

import com.websystemdesign.model.Camera;
import com.websystemdesign.repository.CameraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CameraService {
    private final CameraRepository cameraRepository;

    @Autowired
    public CameraService(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    public List<Camera> getAllCamera() {
        return cameraRepository.findAll();
    }

    public Optional<Camera> getRoomById(Long id) {
        return cameraRepository.findById(id);
    }

    public Camera saveRoom(Camera room) {
        return cameraRepository.save(room);
    }

    public void deleteRoom(Long id) {
        cameraRepository.deleteById(id);
    }

    public List<Camera> getCamereBySede(Long sedeId) {
        return cameraRepository.findBySedeId(sedeId);
    }

    // Restituisce la lista delle immagini disponibili per la camera
    public List<String> getImmaginiCamera(Camera camera) {
        String tipo = camera.getTipologia() != null && camera.getTipologia().equalsIgnoreCase("Suite") ? "Suite" : "Rooms";
        int maxFolder = 12; 
        
        long folderIndex = (camera.getId() % maxFolder);
        if (folderIndex == 0) folderIndex = maxFolder;

        String relativePath = "static/image/" + tipo + "/" + folderIndex;
        String webPath = "/image/" + tipo + "/" + folderIndex + "/";
        
        List<String> immagini = new ArrayList<>();
        
        try {
            // Cerchiamo la cartella nelle risorse
            File folder = new ClassPathResource(relativePath).getFile();
            
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".webp") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));
                
                if (files != null) {
                    // Aggiungiamo tutti i file trovati
                    for (File file : files) {
                        immagini.add(webPath + file.getName());
                    }
                }
            }
        } catch (Exception e) {
            // Fallback in caso di errore (es. esecuzione da JAR dove getFile() non funziona)
            // O se la cartella non viene trovata.
            // Mettiamo almeno un'immagine di default o proviamo i primi 3 numeri
            System.err.println("Errore nel recupero immagini per camera " + camera.getId() + ": " + e.getMessage());
            for (int i = 1; i <= 3; i++) {
                immagini.add(webPath + i + ".webp");
            }
        }
        
        // Se non abbiamo trovato nulla (lista vuota), fallback
        if (immagini.isEmpty()) {
             immagini.add(webPath + "1.webp");
        }
        
        return immagini;
    }
}
