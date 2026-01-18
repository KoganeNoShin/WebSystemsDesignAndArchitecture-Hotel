package com.websystemdesign.service;

import com.websystemdesign.model.Ospite;
import com.websystemdesign.repository.OspiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OspiteService {

    private final OspiteRepository ospiteRepository;

    @Autowired
    public OspiteService(OspiteRepository ospiteRepository) {
        this.ospiteRepository = ospiteRepository;
    }

    public List<Ospite> getAllOspiti() {
        return ospiteRepository.findAll();
    }

    public Optional<Ospite> getOspiteById(Long id) {
        return ospiteRepository.findById(id);
    }

    public Ospite saveOspite(Ospite ospite) {
        return ospiteRepository.save(ospite);
    }

    public void deleteOspite(Long id) {
        ospiteRepository.deleteById(id);
    }
}
