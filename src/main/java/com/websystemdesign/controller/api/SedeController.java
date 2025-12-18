package com.websystemdesign.controller.api;

import com.websystemdesign.dto.SedeDto;
import com.websystemdesign.mapper.SedeMapper;
import com.websystemdesign.model.Sede;
import com.websystemdesign.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sedi")
public class SedeController {

    private final SedeService sedeService;
    private final SedeMapper sedeMapper;

    @Autowired
    public SedeController(SedeService sedeService, SedeMapper sedeMapper) {
        this.sedeService = sedeService;
        this.sedeMapper = sedeMapper;
    }

    @GetMapping
    public List<SedeDto> getAllSedi() {
        return sedeService.getAllSedi().stream()
                .map(sedeMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SedeDto getSedeById(@PathVariable Long id) {
        return sedeService.getSedeById(id)
                .map(sedeMapper::toDto)
                .orElse(null);
    }

    @PostMapping
    public SedeDto createSede(@RequestBody SedeDto sedeDto) {
        Sede sede = sedeMapper.toEntity(sedeDto);
        Sede savedSede = sedeService.saveSede(sede);
        return sedeMapper.toDto(savedSede);
    }

    @DeleteMapping("/{id}")
    public void deleteSede(@PathVariable Long id) {
        sedeService.deleteSede(id);
    }
}
