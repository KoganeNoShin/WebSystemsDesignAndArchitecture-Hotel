package com.websystemdesign.controller.api;

import com.websystemdesign.dto.BookingDetailDto;
import com.websystemdesign.model.Cliente;
import com.websystemdesign.model.Multimedia;
import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.model.Service;
import com.websystemdesign.model.Utente;
import com.websystemdesign.repository.ClienteRepository;
import com.websystemdesign.repository.PrenotazioneRepository;
import com.websystemdesign.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cliente")
public class ApiClienteController {

    private final PrenotazioneRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public ApiClienteController(PrenotazioneRepository prenotazioneRepository, UtenteRepository utenteRepository, ClienteRepository clienteRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/booking/{id}")
    public BookingDetailDto getBookingDetails(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utente utente = utenteRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Cliente cliente = clienteRepository.findByUtenteId(utente.getId()).orElseThrow();

        Prenotazione p = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        if (!p.getCliente().getId().equals(cliente.getId())) {
            throw new SecurityException("Accesso negato alla prenotazione");
        }

        BookingDetailDto dto = new BookingDetailDto();
        dto.setId(p.getId());
        dto.setStato(p.getStato().name());
        
        dto.setCameraNumero(p.getCamera().getNumero());
        dto.setCameraTipologia(p.getCamera().getTipologia());
        dto.setSedeNome(p.getCamera().getSede().getNome());
        dto.setSedeLocation(p.getCamera().getSede().getLocation());
        
        dto.setCheckin(p.getDataInizio());
        dto.setCheckout(p.getDataFine());
        
        // Calcolo Costi
        float costoServizi = 0.0f;
        if (p.getServices() != null) {
            costoServizi = (float) p.getServices().stream().mapToDouble(Service::getCosto).sum();
        }
        
        float costoMultimedia = 0.0f;
        if (p.getMultimedia() != null) {
            costoMultimedia = (float) p.getMultimedia().stream().mapToDouble(Multimedia::getCosto).sum();
        }
        
        // Ricalcoliamo il costo base della camera per sicurezza (o sottraiamo dal totale se ci fidiamo del totale)
        // Meglio ricalcolare: PrezzoBase * Notti
        long nights = ChronoUnit.DAYS.between(p.getDataInizio(), p.getDataFine());
        if (nights < 1) nights = 1;
        float costoCamera = p.getCamera().getPrezzoBase() * nights;
        
        // Il totale salvato nel DB dovrebbe essere la somma di tutto.
        // Se il DB è disallineato, usiamo la somma calcolata ora o quella del DB?
        // Usiamo quella del DB come "Totale Dovuto", ma mostriamo i dettagli calcolati.
        // Se c'è discrepanza, potrebbe essere un problema, ma per ora mostriamo i dettagli.
        
        dto.setCostoCamera(costoCamera);
        dto.setCostoServizi(costoServizi);
        dto.setCostoMultimedia(costoMultimedia);
        dto.setCostoTotale(p.getCosto()); // Totale dal DB
        
        dto.setServizi(p.getServices().stream()
                .map(s -> s.getNome() + " (€ " + s.getCosto() + ")")
                .collect(Collectors.toList()));
        
        if (p.getOspiti() != null) {
            dto.setOspiti(p.getOspiti().stream().map(o -> {
                BookingDetailDto.OspiteDto od = new BookingDetailDto.OspiteDto();
                od.setNome(o.getNome());
                od.setCognome(o.getCognome());
                od.setCittadinanza(o.getCittadinanza());
                od.setDataNascita(o.getDataNascita().toString());
                return od;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
