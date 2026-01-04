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

    /**
     * Esegue ogni ora.
     * 1. Cancella prenotazioni non confermate (Check-in mancato entro le 11:00 del giorno arrivo).
     * 2. Imposta camere DA_PULIRE per check-out scaduti (dopo le 11:00 del giorno partenza).
     */
    @Scheduled(cron = "0 0 * * * ?") // Ogni ora
    @Transactional
    public void scheduledTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll(); // In prod usare query specifica!
        
        for (Prenotazione p : prenotazioni) {
            // 1. Cancellazione Check-in mancato
            if (p.getStato() == StatoPrenotazione.CONFERMATA) {
                LocalDateTime deadlineCheckin = p.getDataInizio().atTime(11, 0); // O 12:00 come da nuovo requisito? "Entro le 12:00 il nuovo cliente deve fare il CHECK IN"
                // Mettiamo 12:00 per sicurezza se la regola è cambiata, o lasciamo 11:00.
                // "Entro le 12:00 il nuovo cliente deve fare il CHECK IN" -> Deadline 12:00
                deadlineCheckin = p.getDataInizio().atTime(12, 0);
                
                if (now.isAfter(deadlineCheckin)) {
                    p.setStato(StatoPrenotazione.CANCELLATA);
                    prenotazioneRepository.save(p);
                }
            }
            
            // 2. Auto-Set Camera DA_PULIRE per Check-out scaduti
            if (p.getStato() == StatoPrenotazione.CHECKED_IN && p.getDataFine().isEqual(today)) {
                LocalDateTime deadlineCheckout = p.getDataFine().atTime(11, 0);
                
                if (now.isAfter(deadlineCheckout)) {
                    // Se il cliente non ha fatto check-out, la camera diventa comunque DA_PULIRE
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
