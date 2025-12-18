package com.websystemdesign.service;

import com.websystemdesign.model.Dipendente;
import com.websystemdesign.model.Utente;
import com.websystemdesign.repository.ClienteRepository;
import com.websystemdesign.repository.DipendenteRepository;
import com.websystemdesign.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UtenteService implements UserDetailsService {

    private final UtenteRepository utenteRepository;
    private final DipendenteRepository dipendenteRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public UtenteService(UtenteRepository utenteRepository, DipendenteRepository dipendenteRepository, ClienteRepository clienteRepository) {
        this.utenteRepository = utenteRepository;
        this.dipendenteRepository = dipendenteRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con username: " + username));

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // Cerchiamo se è un dipendente per assegnare il ruolo
        Optional<Dipendente> dipendenteOpt = dipendenteRepository.findByUtenteId(utente.getId());
        if (dipendenteOpt.isPresent()) {
            // Spring Security richiede il prefisso "ROLE_"
            authorities.add(new SimpleGrantedAuthority("ROLE_" + dipendenteOpt.get().getRuolo().name()));
        } else {
            // Se non è un dipendente, assumiamo sia un cliente
            clienteRepository.findByUtenteId(utente.getId()).ifPresent(cliente -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_CLIENTE"));
            });
        }

        return new User(utente.getUsername(), utente.getPassword(), authorities);
    }

    public List<Utente> getAllUtenti() {
        return utenteRepository.findAll();
    }
    
    // ... altri metodi del service ...
    public Optional<Utente> getUtenteById(Long id) { return utenteRepository.findById(id); }
    public Optional<Utente> getUtenteByUsername(String username) { return utenteRepository.findByUsername(username); }
    public Utente saveUtente(Utente utente) { return utenteRepository.save(utente); }
    public void deleteUtente(Long id) { utenteRepository.deleteById(id); }
}
