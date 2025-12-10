package com.websystemdesign.controller.api;

import com.websystemdesign.model.Utente;
import com.websystemdesign.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UtenteController {

    private final UtenteService utenteService;

    @Autowired
    public UtenteController(UtenteService userService){
        this.utenteService = userService;
    }

    // ??? a che serve?
    @GetMapping
    public List<Utente> getAllUtenti(){
        return utenteService.getAllUtenti();
    }

    @GetMapping("/id/{id}")
    public Optional<Utente> getUtenteByID(@PathVariable Long id){
        return utenteService.getUtenteById(id);
    }

    @GetMapping("/username/{username}")
    public Optional<Utente> getUtenteByUsername(@PathVariable String username){
        return utenteService.getUtenteByUsername(username);
    }

    @PostMapping
    public Utente saveUtente(@RequestBody Utente user){
        return utenteService.saveUtente(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUtente(@PathVariable Long id){
        utenteService.deleteUtente(id);
    }
}
