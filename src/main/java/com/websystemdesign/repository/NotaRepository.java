package com.websystemdesign.repository;

import com.websystemdesign.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByPrenotazioneIdOrderByDataCreazioneDesc(Long prenotazioneId);
}
