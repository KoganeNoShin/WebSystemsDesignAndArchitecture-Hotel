package com.websystemdesign.controller.api;

import com.websystemdesign.dto.NotaDto;
import com.websystemdesign.mapper.NotaMapper;
import com.websystemdesign.model.Cliente;
import com.websystemdesign.model.Nota;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.Utente;
import com.websystemdesign.repository.ClienteRepository;
import com.websystemdesign.repository.NotaRepository;
import com.websystemdesign.repository.PrenotazioneRepository;
import com.websystemdesign.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/cliente/note")
public class NoteController {

    private final NotaRepository notaRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final ClienteRepository clienteRepository;
    private final NotaMapper notaMapper;

    @Autowired
    public NoteController(NotaRepository notaRepository, PrenotazioneRepository prenotazioneRepository, UtenteRepository utenteRepository, ClienteRepository clienteRepository, NotaMapper notaMapper) {
        this.notaRepository = notaRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.clienteRepository = clienteRepository;
        this.notaMapper = notaMapper;
    }

    @GetMapping("/{prenotazioneId}")
    public List<NotaDto> getNote(@PathVariable Long prenotazioneId,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                 @RequestParam(defaultValue = "10") String limit,
                                 Authentication authentication) {
        
        checkOwnership(prenotazioneId, authentication);
        
        Stream<Nota> stream = notaRepository.findByPrenotazioneIdOrderByDataCreazioneDesc(prenotazioneId).stream();

        if (from != null) {
            stream = stream.filter(n -> n.getDataCreazione().isAfter(from) || n.getDataCreazione().isEqual(from));
        }
        if (to != null) {
            stream = stream.filter(n -> n.getDataCreazione().isBefore(to) || n.getDataCreazione().isEqual(to));
        }

        if (!"all".equalsIgnoreCase(limit)) {
            try {
                int max = Integer.parseInt(limit);
                stream = stream.limit(max);
            } catch (NumberFormatException e) {
            }
        }

        return stream.map(notaMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping("/add")
    public NotaDto addNota(@RequestParam Long prenotazioneId, @RequestParam String testo, Authentication authentication) {
        Prenotazione p = checkOwnership(prenotazioneId, authentication);
        
        Nota nota = new Nota(testo, p);
        nota.setDataCreazione(LocalDateTime.now());
        notaRepository.save(nota);
        
        return notaMapper.toDto(nota);
    }

    private Prenotazione checkOwnership(Long prenotazioneId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteRepository.findByUtenteId(utente.getId()).orElseThrow();
        
        Prenotazione p = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));
        
        if (!p.getCliente().getId().equals(cliente.getId())) {
            throw new SecurityException("Accesso negato");
        }
        return p;
    }
}
