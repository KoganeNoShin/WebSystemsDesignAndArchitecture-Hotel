package com.websystemdesign.controller.api;

import com.websystemdesign.model.Prenotazione;
import com.websystemdesign.service.PrenotazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/booking")
public class PrenotazioneController {
    private final PrenotazioneService prenotazioneService;

    @Autowired
    public PrenotazioneController(PrenotazioneService bookingService){
        this.prenotazioneService = bookingService;
    }

    // di tutte le sedi? non serve a molto se non si specializza
    @GetMapping
    public List<Prenotazione> getAllPrenotazioni(){
        return prenotazioneService.getAllPrenotazioni();
    }

    @GetMapping("/user/{id}")
    public List<Prenotazione> getPrenotazioniByCliente(@PathVariable Long id){
        return prenotazioneService.getPrenotazioniByCliente(id);
    }

    @GetMapping("/id/{id}")
    public Optional<Prenotazione> getPrenotazioneByID(@PathVariable Long id){
        return prenotazioneService.getPrenotazioneById(id);
    }

    @PostMapping
    public Prenotazione createPrenotazione(@RequestBody Prenotazione booking){
        return prenotazioneService.savePrenotazione(booking);
    }

    // Legalmente folle poter "cancellare" una prenotazione dal DB, lo lasciamo ma non è per niente una best practice
    @DeleteMapping("/{id}")
    public void deletePrenotazione(@PathVariable Long id){
        prenotazioneService.deletePrenotazione(id);
    }
}
