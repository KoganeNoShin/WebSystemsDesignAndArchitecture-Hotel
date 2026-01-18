package com.websystemdesign.controller.api;

import com.websystemdesign.dto.UtenteDto;
import com.websystemdesign.mapper.UtenteMapper;
import com.websystemdesign.model.Utente;
import com.websystemdesign.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    private final UtenteService utenteService;
    private final UtenteMapper utenteMapper;

    @Autowired
    public UtenteController(UtenteService utenteService, UtenteMapper utenteMapper) {
        this.utenteService = utenteService;
        this.utenteMapper = utenteMapper;
    }

    @GetMapping
    public List<UtenteDto> getAllUtenti() {
        return utenteService.getAllUtenti().stream()
                .map(utenteMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UtenteDto getUtenteById(@PathVariable Long id) {
        return utenteService.getUtenteById(id)
                .map(utenteMapper::toDto)
                .orElse(null);
    }

    @PostMapping
    public UtenteDto createUtente(@Valid @RequestBody UtenteDto utenteDto) {
        Utente utente = utenteMapper.toEntity(utenteDto);
        Utente savedUtente = utenteService.saveUtente(utente);
        return utenteMapper.toDto(savedUtente);
    }

    @DeleteMapping("/{id}")
    public void deleteUtente(@PathVariable Long id) {
        utenteService.deleteUtente(id);
    }
}
