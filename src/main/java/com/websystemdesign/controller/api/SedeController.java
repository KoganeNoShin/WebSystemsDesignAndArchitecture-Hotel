package com.websystemdesign.controller.api;

import com.websystemdesign.model.Sede;
import com.websystemdesign.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/buildings")
public class SedeController {

    private final SedeService sedeService;

    @Autowired
    public SedeController(SedeService buildingService){
        this.sedeService = buildingService;
    }

    @GetMapping
    public List<Sede> getAllSedi() {
        return sedeService.getAllSedi();
    }

    @GetMapping("/{id}")
    public Optional<Sede> getSedeByID(@PathVariable Long id){
        return sedeService.getSedeById(id);
    }

    //Mi sa che conviene proteggere questa rotta
    @PostMapping
    public Sede createSede(@RequestBody Sede building){
        return  sedeService.saveSede(building);
    }

    //Non sia mai che una sede ci viene pignorata o abbattuta in guerra
    @DeleteMapping("/{id}")
    public void deleteSede(@PathVariable Long id){
        sedeService.deleteSede(id);
    }
}
