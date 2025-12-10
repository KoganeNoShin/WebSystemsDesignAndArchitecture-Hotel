package com.websystemdesign.controller.api;

import com.websystemdesign.model.Dipendente;
import com.websystemdesign.service.DipendenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
public class DipendenteController {

    private final DipendenteService dipendenteService;

    @Autowired
    public DipendenteController(DipendenteService employeeService){
        this.dipendenteService = employeeService;
    }

    @GetMapping
    public List<Dipendente> getAllDipendenti(){
        return dipendenteService.getAllDipendenti();
    }

    @GetMapping("/{id}")
    public Optional<Dipendente> getDipendenteByID(@PathVariable Long id){
        return dipendenteService.getDipendenteById(id);
    }

    @PostMapping
    public Dipendente saveDipendente(@RequestBody Dipendente employee){
        return dipendenteService.saveDipendente(employee);
    }

    @DeleteMapping("/{id}")
    public void deleteDipendente(@PathVariable Long id){
        dipendenteService.deleteDipendente(id);
    }
}
