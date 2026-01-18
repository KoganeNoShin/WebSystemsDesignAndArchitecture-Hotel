package com.websystemdesign.repository;

import com.websystemdesign.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    List<Prenotazione> findByCameraIdAndDataInizioBeforeAndDataFineAfter(Long camera_id, LocalDate dataFine, LocalDate dataInizio);

    List<Prenotazione> findByClienteId(Long cliente_id);

    @Query("SELECT p FROM Prenotazione p WHERE p.camera.id = :cameraId " +
            "AND p.dataInizio < :dataFine AND p.dataFine > :dataInizio " +
            "AND p.stato != 'CANCELLATA'")
    List<Prenotazione> findSovrapposizioni(Long cameraId, LocalDate dataFine, LocalDate dataInizio);

    @Query("SELECT p FROM Prenotazione p WHERE p.camera.id = :cameraId " +
            "AND (:startDate IS NULL OR p.dataInizio >= :startDate) " +
            "AND (:endDate IS NULL OR p.dataFine <= :endDate) " +
            "ORDER BY p.dataInizio DESC")
    List<Prenotazione> findByCameraIdAndFilters(Long cameraId, LocalDate startDate, LocalDate endDate);
}
