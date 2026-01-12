package com.websystemdesign.service;

import com.websystemdesign.model.Camera;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoCamera;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.repository.CameraRepository;
import com.websystemdesign.repository.PrenotazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingCleanupService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final CameraRepository cameraRepository;

    @Autowired
    public BookingCleanupService(PrenotazioneRepository prenotazioneRepository, CameraRepository cameraRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.cameraRepository = cameraRepository;
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void scheduledTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll();
        
        for (Prenotazione p : prenotazioni) {
            if (p.getStato() == StatoPrenotazione.CONFERMATA) {
                LocalDateTime deadlineCheckin = p.getDataInizio().atTime(12, 0);
                
                if (now.isAfter(deadlineCheckin)) {
                    p.setStato(StatoPrenotazione.CANCELLATA);
                    prenotazioneRepository.save(p);
                }
            }
            
            if (p.getStato() == StatoPrenotazione.CHECKED_IN && p.getDataFine().isEqual(today)) {
                LocalDateTime deadlineCheckout = p.getDataFine().atTime(11, 0);
                
                if (now.isAfter(deadlineCheckout)) {
                    Camera c = p.getCamera();
                    if (c.getStatus() != StatoCamera.DA_PULIRE) {
                        c.setStatus(StatoCamera.DA_PULIRE);
                        cameraRepository.save(c);
                    }
                }
            }
        }
    }
}
