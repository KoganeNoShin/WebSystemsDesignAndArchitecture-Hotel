package com.websystemdesign.service;

import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.repository.PrenotazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepository;

    @Autowired
    public PrenotazioneService(PrenotazioneRepository prenotazioneRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
    }

    public List<Prenotazione> getAllPrenotazioni() {
        return prenotazioneRepository.findAll();
    }

    public Optional<Prenotazione> getPrenotazioneById(Long id) {
        return prenotazioneRepository.findById(id);
    }

    public Prenotazione savePrenotazione(Prenotazione prenotazione) {
        if (prenotazione.getId() == null && prenotazione.getStato() == null) {
            prenotazione.setStato(StatoPrenotazione.CONFERMATA);
        }

        return prenotazioneRepository.save(prenotazione);
    }

    public void deletePrenotazione(Long id) {
        prenotazioneRepository.deleteById(id);
    }

    public List<Prenotazione> getPrenotazioniByCliente(Long clienteId) {
        return prenotazioneRepository.findByClienteId(clienteId);
    }

    public boolean isCameraDisponibile(Long camera_id, LocalDate inizio, LocalDate fine) {
        List<Prenotazione> sovrapposizioni = prenotazioneRepository.findSovrapposizioni(camera_id, fine, inizio);
        return sovrapposizioni.isEmpty();
    }

    public List<Prenotazione> getPrenotazioniByCameraAndDates(Long cameraId, LocalDate start, LocalDate end) {
        return prenotazioneRepository.findByCameraIdAndFilters(cameraId, start, end);
    }
}
