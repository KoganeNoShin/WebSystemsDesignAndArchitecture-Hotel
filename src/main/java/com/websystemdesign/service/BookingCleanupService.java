package com.websystemdesign.service;

import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.repository.PrenotazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingCleanupService {

    private final PrenotazioneRepository prenotazioneRepository;

    @Autowired
    public BookingCleanupService(PrenotazioneRepository prenotazioneRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
    }

    /**
     * Esegue ogni notte a mezzanotte (00:00).
     * Controlla le prenotazioni che iniziano "domani".
     * Se non hanno fatto il check-in (stato CONFERMATA), le cancella.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Ogni giorno a mezzanotte
    @Transactional
    public void cancelUnconfirmedBookings() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        // Trova prenotazioni che iniziano domani e sono ancora in stato CONFERMATA (non CHECKED_IN)
        // Nota: Dovremmo avere un metodo repository specifico o usare stream
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll(); // In prod usare query specifica!
        
        int cancelledCount = 0;
        for (Prenotazione p : prenotazioni) {
            if (p.getDataInizio().isEqual(tomorrow) && p.getStato() == StatoPrenotazione.CONFERMATA) {
                p.setStato(StatoPrenotazione.CANCELLATA);
                prenotazioneRepository.save(p);
                cancelledCount++;
            }
        }
        
        if (cancelledCount > 0) {
            System.out.println("BookingCleanupService: Cancellate " + cancelledCount + " prenotazioni non confermate per domani.");
        }
    }
}
