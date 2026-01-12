package com.websystemdesign.service;

import com.websystemdesign.dto.DipendenteDto;
import com.websystemdesign.model.Dipendente;
import com.websystemdesign.model.Sede;
import com.websystemdesign.model.Utente;
import com.websystemdesign.repository.DipendenteRepository;
import com.websystemdesign.repository.SedeRepository;
import com.websystemdesign.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DipendenteService {

    private final DipendenteRepository dipendenteRepository;
    private final UtenteRepository utenteRepository;
    private final SedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DipendenteService(DipendenteRepository dipendenteRepository, UtenteRepository utenteRepository, SedeRepository sedeRepository, PasswordEncoder passwordEncoder) {
        this.dipendenteRepository = dipendenteRepository;
        this.utenteRepository = utenteRepository;
        this.sedeRepository = sedeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String[] registraNuovoDipendente(DipendenteDto dto) {
        String baseUsername = dto.getNome().toLowerCase().replaceAll("\\s+", "") + "." + 
                              dto.getCognome().toLowerCase().replaceAll("\\s+", "");
        String finalUsername = baseUsername;
        int counter = 1;
        
        while (utenteRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = baseUsername + String.format("%02d", counter);
            counter++;
        }

        String rawPassword = UUID.randomUUID().toString().substring(0, 8);
        
        Utente nuovoUtente = new Utente();
        nuovoUtente.setNome(dto.getNome());
        nuovoUtente.setCognome(dto.getCognome());
        nuovoUtente.setUsername(finalUsername);
        nuovoUtente.setPassword(passwordEncoder.encode(rawPassword));
        utenteRepository.save(nuovoUtente);

        Sede sede = sedeRepository.findById(dto.getSedeId())
                .orElseThrow(() -> new RuntimeException("Sede non trovata"));

        Dipendente nuovoDipendente = new Dipendente(dto.getRuolo(), nuovoUtente);
        nuovoDipendente.setSede(sede);
        
        dipendenteRepository.save(nuovoDipendente);
        
        return new String[]{finalUsername, rawPassword};
    }

    @Transactional
    public void licenziaDipendente(Long dipendenteId) {
        Dipendente dipendente = dipendenteRepository.findById(dipendenteId)
                .orElseThrow(() -> new RuntimeException("Dipendente non trovato"));
        
        Utente utente = dipendente.getUtente();
        
        dipendenteRepository.delete(dipendente);
        utenteRepository.delete(utente);
    }

    public List<Dipendente> getAllDipendenti() {
        return dipendenteRepository.findAll();
    }

    public Optional<Dipendente> getDipendenteById(Long id) {
        return dipendenteRepository.findById(id);
    }

    public Dipendente saveDipendente(Dipendente dipendente) {
        return dipendenteRepository.save(dipendente);
    }

    public void deleteDipendente(Long id) {
        dipendenteRepository.deleteById(id);
    }
}
