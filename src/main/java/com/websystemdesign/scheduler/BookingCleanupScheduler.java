package com.websystemdesign.scheduler;

import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.service.PrenotazioneService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingCleanupScheduler {

    private final PrenotazioneService prenotazioneService;

    public BookingCleanupScheduler(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void cancelNoShowBookings() {
        LocalDate oggi = LocalDate.now();

        List<Prenotazione> noShow = prenotazioneService.getAllPrenotazioni().stream()
                .filter(p -> p.getStato() == StatoPrenotazione.CONFERMATA)
                .filter(p -> p.getDataInizio().isEqual(oggi))
                .collect(Collectors.toList());

        for (Prenotazione p : noShow) {
            p.setStato(StatoPrenotazione.CANCELLATA);
            prenotazioneService.savePrenotazione(p);
            System.out.println("Scheduler: Prenotazione " + p.getId() + " CANCELLATA per mancato Check-in (No-Show)");
        }
    }
}
