package com.websystemdesign.repository;

import com.websystemdesign.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    // Trova tutte le prenotazioni per una data camera che si sovrappongono a un dato intervallo di date
    List<Prenotazione> findByCameraIdAndDataInizioBeforeAndDataFineAfter(Long camera_id, LocalDate dataFine, LocalDate dataInizio);

    // Trova tutte le prenotazioni di un cliente
    List<Prenotazione> findByClienteId(Long cliente_id);
}
