package com.websystemdesign.service;

import com.websystemdesign.model.Sede;
import com.websystemdesign.repository.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SedeService {

    private final SedeRepository sedeRepository;

    @Autowired
    public SedeService(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
    }

    public List<Sede> getAllSedi() {
        return sedeRepository.findAll();
    }

    public Optional<Sede> getSedeById(Long id) {
        return sedeRepository.findById(id);
    }

    public Sede saveSede(Sede sede) {
        return sedeRepository.save(sede);
    }

    public void deleteSede(Long id) {
        sedeRepository.deleteById(id);
    }
}
