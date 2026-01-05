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

    /**
     * Ore 11:00 - CHECK-OUT FORZATO (Logica di perdita controllo camera)
     * Cerca tutte le prenotazioni che finiscono OGGI e che non hanno ancora fatto check-out.
     * Imposta la camera su DA_PULIRE.
     */
    @Scheduled(cron = "0 0 11 * * *") // Ogni giorno alle 11:00:00
    @Transactional
    public void forceRoomCleanupStatus() {
        LocalDate oggi = LocalDate.now();

        // Cerchiamo prenotazioni che finiscono oggi
        // Nota: questa query va ottimizzata nel repository in un progetto reale, qui facciamo stream per semplicità
        List<Prenotazione> inScadenza = prenotazioneRepository.findAll().stream()
                .filter(p -> p.getDataFine().equals(oggi))
                .filter(p -> p.getStato() != StatoPrenotazione.CHECKED_OUT)
                .toList();

        for (Prenotazione p : inScadenza) {
            Camera c = p.getCamera();
            // Anche se il cliente non ha fatto check-out formale, la camera deve essere liberata per le pulizie
            if (c.getStatus() != StatoCamera.DA_PULIRE) {
                c.setStatus(StatoCamera.DA_PULIRE);
                cameraRepository.save(c);
                System.out.println("Scheduler: Camera " + c.getNumero() + " impostata DA_PULIRE (Timeout 11:00)");
            }
        }
    }
}
