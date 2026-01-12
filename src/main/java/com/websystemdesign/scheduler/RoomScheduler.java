package com.websystemdesign.scheduler;

import com.websystemdesign.model.Camera;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoCamera;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.repository.CameraRepository;
import com.websystemdesign.repository.PrenotazioneRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class RoomScheduler {

    private final PrenotazioneRepository prenotazioneRepository;
    private final CameraRepository cameraRepository;

    public RoomScheduler(PrenotazioneRepository prenotazioneRepository, CameraRepository cameraRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.cameraRepository = cameraRepository;
    }

    @Scheduled(cron = "0 0 11 * * *")
    @Transactional
    public void forceRoomCleanupStatus() {
        LocalDate oggi = LocalDate.now();

        List<Prenotazione> inScadenza = prenotazioneRepository.findAll().stream()
                .filter(p -> p.getDataFine().equals(oggi))
                .filter(p -> p.getStato() != StatoPrenotazione.CHECKED_OUT)
                .toList();

        for (Prenotazione p : inScadenza) {
            Camera c = p.getCamera();
            if (c.getStatus() != StatoCamera.DA_PULIRE) {
                c.setStatus(StatoCamera.DA_PULIRE);
                cameraRepository.save(c);
                System.out.println("Scheduler: Camera " + c.getNumero() + " impostata DA_PULIRE (Timeout 11:00)");
            }
        }
    }
}
