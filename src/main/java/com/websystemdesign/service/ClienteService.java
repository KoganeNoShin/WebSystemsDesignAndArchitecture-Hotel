package com.websystemdesign.service;

import com.websystemdesign.dto.ClienteProfileDto;
import com.websystemdesign.model.Cliente;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.StatoPrenotazione;
import com.websystemdesign.model.TipoDocumento;
import com.websystemdesign.model.Utente;
import com.websystemdesign.repository.ClienteRepository;
import com.websystemdesign.repository.PrenotazioneRepository;
import com.websystemdesign.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UtenteRepository utenteRepository;
    private final PrenotazioneRepository prenotazioneRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, UtenteRepository utenteRepository, PrenotazioneRepository prenotazioneRepository) {
        this.clienteRepository = clienteRepository;
        this.utenteRepository = utenteRepository;
        this.prenotazioneRepository = prenotazioneRepository;
    }

    public List<Cliente> getAllClienti() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> getClienteById(Long id) {
        return clienteRepository.findById(id);
    }
    
    public Optional<Cliente> getClienteByUsername(String username) {
        return utenteRepository.findByUsername(username)
                .flatMap(utente -> clienteRepository.findByUtenteId(utente.getId()));
    }

    public Cliente saveCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    @Transactional
    public void updateClienteProfile(String username, ClienteProfileDto dto) {
        Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        
        Cliente cliente = clienteRepository.findByUtenteId(utente.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato"));

        cliente.setCittadinanza(dto.getCittadinanza());
        cliente.setLuogo(dto.getLuogoNascita());
        
        if (dto.getDataNascita() != null) {
            cliente.setDataNascita(dto.getDataNascita().toString());
        } else {
            cliente.setDataNascita(null);
        }
        
        if (dto.getTipoDocumento() != null) {
            cliente.setTipoDocumento(dto.getTipoDocumento().getDescrizione());
        } else {
            cliente.setTipoDocumento(null);
        }

        cliente.setNumDocumento(dto.getNumDocumento());

        clienteRepository.save(cliente);
    }
    
    public boolean canBook(Long clienteId) {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findByClienteId(clienteId);
        return prenotazioni.stream()
                .noneMatch(p -> p.getStato() != StatoPrenotazione.CHECKED_OUT && p.getStato() != StatoPrenotazione.CANCELLATA);
    }
}
