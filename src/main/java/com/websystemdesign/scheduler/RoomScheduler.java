package com.websystemdesign.scheduler;

import com.websystemdesign.model.Camera;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoCamera;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.service.CameraService;
import com.websystemdesign.service.PrenotazioneService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomScheduler {

    private final PrenotazioneService prenotazioneService;
    private final CameraService cameraService;

    public RoomScheduler(PrenotazioneService prenotazioneService, CameraService cameraService) {
        this.prenotazioneService = prenotazioneService;
        this.cameraService = cameraService;
    }

    @Scheduled(cron = "0 0 11 * * *")
    @Transactional
    public void forceRoomCleanupStatus() {
        LocalDate oggi = LocalDate.now();

        List<Prenotazione> inScadenza = prenotazioneService.getAllPrenotazioni().stream()
                .filter(p -> p.getDataFine().equals(oggi))
                .filter(p -> p.getStato() != StatoPrenotazione.CHECKED_OUT)
                .collect(Collectors.toList());

        for (Prenotazione p : inScadenza) {
            Camera c = p.getCamera();
            if (c.getStatus() != StatoCamera.DA_PULIRE) {
                c.setStatus(StatoCamera.DA_PULIRE);
                cameraService.saveRoom(c);
                System.out.println("Scheduler: Camera " + c.getNumero() + " impostata DA_PULIRE (Timeout 11:00)");
            }
        }
    }
}
