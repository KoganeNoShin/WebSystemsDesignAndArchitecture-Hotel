package com.websystemdesign.controller.api;

import com.websystemdesign.dto.NotaDto;
import com.websystemdesign.mapper.NotaMapper;
import com.websystemdesign.model.Cliente;
import com.websystemdesign.model.Nota;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.Utente;
import com.websystemdesign.service.ClienteService;
import com.websystemdesign.service.NotaService;
import com.websystemdesign.service.PrenotazioneService;
import com.websystemdesign.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cliente/note")
public class NoteController {

    private final NotaService notaService;
    private final PrenotazioneService prenotazioneService;
    private final UtenteService utenteService;
    private final ClienteService clienteService;
    private final NotaMapper notaMapper;

    @Autowired
    public NoteController(NotaService notaService, PrenotazioneService prenotazioneService, UtenteService utenteService, ClienteService clienteService, NotaMapper notaMapper) {
        this.notaService = notaService;
        this.prenotazioneService = prenotazioneService;
        this.utenteService = utenteService;
        this.clienteService = clienteService;
        this.notaMapper = notaMapper;
    }

    @GetMapping("/{prenotazioneId}")
    public List<NotaDto> getNote(@PathVariable Long prenotazioneId,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                 @RequestParam(defaultValue = "10") String limit,
                                 Authentication authentication) {
        
        checkOwnership(prenotazioneId, authentication);
        
        List<Nota> note = notaService.getNote(prenotazioneId, from, to, limit);

        return note.stream().map(notaMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping("/add")
    public NotaDto addNota(@Valid @RequestBody NotaDto notaDto, Authentication authentication) {
        Prenotazione p = checkOwnership(notaDto.getPrenotazioneId(), authentication);
        
        Nota nota = notaService.addNota(notaDto.getTesto(), p);
        
        return notaMapper.toDto(nota);
    }

    private Prenotazione checkOwnership(Long prenotazioneId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteService.getUtenteByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteService.getClienteByUsername(utente.getUsername()).orElseThrow();
        
        Prenotazione p = prenotazioneService.getPrenotazioneById(prenotazioneId)
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));
        
        if (!p.getCliente().getId().equals(cliente.getId())) {
            throw new SecurityException("Accesso negato");
        }
        return p;
    }
}
