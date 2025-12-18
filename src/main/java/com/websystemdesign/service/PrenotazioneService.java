package com.websystemdesign.service;

import com.websystemdesign.model.Prenotazione;
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

    // se non è detto che la prenotazione vada a buon fine, bisognerebbe mettere un tipo di ritorno diverso...
    public Prenotazione savePrenotazione(Prenotazione prenotazione) {
        // Qui in futuro andrà la logica per verificare la disponibilità, calcolare il costo, etc.
        return prenotazioneRepository.save(prenotazione);
    }

    public void deletePrenotazione(Long id) {
        prenotazioneRepository.deleteById(id);
    }

    public List<Prenotazione> getPrenotazioniByCliente(Long clienteId) {
        return prenotazioneRepository.findByClienteId(clienteId);
    }

    // questo è un metodo helper? va utilizzato in save prenotazioni? avrebbe più senso metterlo in CameraService
    public boolean isCameraDisponibile(Long camera_id, LocalDate inizio, LocalDate fine) {
        List<Prenotazione> sovrapposizioni = prenotazioneRepository.findByCameraIdAndDataInizioBeforeAndDataFineAfter(camera_id, fine, inizio);
        return sovrapposizioni.isEmpty();
    }
}
