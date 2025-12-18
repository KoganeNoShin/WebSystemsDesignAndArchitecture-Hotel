package com.websystemdesign.controller.api;

import com.websystemdesign.dto.PrenotazioneDto;
import com.websystemdesign.mapper.PrenotazioneMapper;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.service.PrenotazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;
    private final PrenotazioneMapper prenotazioneMapper;

    @Autowired
    public PrenotazioneController(PrenotazioneService prenotazioneService, PrenotazioneMapper prenotazioneMapper) {
        this.prenotazioneService = prenotazioneService;
        this.prenotazioneMapper = prenotazioneMapper;
    }

    @GetMapping
    public List<PrenotazioneDto> getAllPrenotazioni() {
        return prenotazioneService.getAllPrenotazioni().stream()
                .map(prenotazioneMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PrenotazioneDto getPrenotazioneById(@PathVariable Long id) {
        return prenotazioneService.getPrenotazioneById(id)
                .map(prenotazioneMapper::toDto)
                .orElse(null);
    }

    @PostMapping
    public PrenotazioneDto createPrenotazione(@RequestBody PrenotazioneDto prenotazioneDto) {
        Prenotazione prenotazione = prenotazioneMapper.toEntity(prenotazioneDto);
        Prenotazione savedPrenotazione = prenotazioneService.savePrenotazione(prenotazione);
        return prenotazioneMapper.toDto(savedPrenotazione);
    }

    @DeleteMapping("/{id}")
    public void deletePrenotazione(@PathVariable Long id) {
        prenotazioneService.deletePrenotazione(id);
    }
}
