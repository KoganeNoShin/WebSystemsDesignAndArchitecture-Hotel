package com.websystemdesign.repository;

import com.websystemdesign.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {
    // Metodo per trovare un utente dal suo username (utile per il login)
    Optional<Utente> findByUsername(String username);
}
