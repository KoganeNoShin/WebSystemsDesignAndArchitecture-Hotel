package com.websystemdesign.controller.api;

import com.websystemdesign.dto.DateOccupateDto;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.repository.PrenotazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final PrenotazioneRepository prenotazioneRepository;

    @Autowired
    public AvailabilityController(PrenotazioneRepository prenotazioneRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
    }

    @GetMapping("/camera/{id}")
    public List<DateOccupateDto> getOccupiedDates(@PathVariable Long id) {
        // Recuperiamo tutte le prenotazioni future per la camera
        // Per semplicità prendiamo tutte quelle che finiscono dopo oggi
        // Nota: in un sistema reale useremmo una query specifica per ottimizzare
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll().stream()
                .filter(p -> p.getCamera().getId().equals(id))
                .filter(p -> p.getDataFine().isAfter(LocalDate.now()))
                // Filtriamo eventuali stati cancellati se necessario (es. p.getStato() != CANCELLATA)
                .collect(Collectors.toList());

        return prenotazioni.stream()
                .map(p -> new DateOccupateDto(p.getDataInizio(), p.getDataFine()))
                .collect(Collectors.toList());
    }
}
