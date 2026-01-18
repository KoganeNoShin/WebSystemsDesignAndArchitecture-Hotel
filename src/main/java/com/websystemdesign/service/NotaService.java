package com.websystemdesign.service;

import com.websystemdesign.model.Nota;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.repository.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NotaService {

    private final NotaRepository notaRepository;

    @Autowired
    public NotaService(NotaRepository notaRepository) {
        this.notaRepository = notaRepository;
    }

    public List<Nota> getNote(Long prenotazioneId, LocalDateTime from, LocalDateTime to, String limit) {
        Stream<Nota> stream = notaRepository.findByPrenotazioneIdOrderByDataCreazioneDesc(prenotazioneId).stream();

        if (from != null) {
            stream = stream.filter(n -> n.getDataCreazione().isAfter(from) || n.getDataCreazione().isEqual(from));
        }
        if (to != null) {
            stream = stream.filter(n -> n.getDataCreazione().isBefore(to) || n.getDataCreazione().isEqual(to));
        }

        if (limit != null && !"all".equalsIgnoreCase(limit)) {
            try {
                int max = Integer.parseInt(limit);
                stream = stream.limit(max);
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        return stream.collect(Collectors.toList());
    }

    public Nota addNota(String testo, Prenotazione prenotazione) {
        Nota nota = new Nota(testo, prenotazione);
        nota.setDataCreazione(LocalDateTime.now());
        return notaRepository.save(nota);
    }
}
