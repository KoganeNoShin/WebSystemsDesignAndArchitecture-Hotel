package com.websystemdesign.controller.api;

import com.websystemdesign.dto.DipendenteDto;
import com.websystemdesign.mapper.DipendenteMapper;
import com.websystemdesign.model.Dipendente;
import com.websystemdesign.service.DipendenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dipendenti")
public class DipendenteController {

    private final DipendenteService dipendenteService;
    private final DipendenteMapper dipendenteMapper;

    @Autowired
    public DipendenteController(DipendenteService dipendenteService, DipendenteMapper dipendenteMapper) {
        this.dipendenteService = dipendenteService;
        this.dipendenteMapper = dipendenteMapper;
    }

    @GetMapping
    public List<DipendenteDto> getAllDipendenti() {
        return dipendenteService.getAllDipendenti().stream()
                .map(dipendenteMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DipendenteDto getDipendenteById(@PathVariable Long id) {
        return dipendenteService.getDipendenteById(id)
                .map(dipendenteMapper::toDto)
                .orElse(null);
    }

    @PostMapping
    public DipendenteDto createDipendente(@Valid @RequestBody DipendenteDto dipendenteDto) {
        Dipendente dipendente = dipendenteMapper.toEntity(dipendenteDto);
        Dipendente savedDipendente = dipendenteService.saveDipendente(dipendente);
        return dipendenteMapper.toDto(savedDipendente);
    }

    @DeleteMapping("/{id}")
    public void deleteDipendente(@PathVariable Long id) {
        dipendenteService.deleteDipendente(id);
    }
}
