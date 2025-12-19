package com.websystemdesign.controller.web;

import com.websystemdesign.dto.DipendenteDto;
import com.websystemdesign.mapper.DipendenteMapper;
import com.websystemdesign.service.DipendenteService;
import com.websystemdesign.service.SedeService;
import com.websystemdesign.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/staff")
public class AdminStaffController {

    private final DipendenteService dipendenteService;
    private final DipendenteMapper dipendenteMapper;
    private final SedeService sedeService;
    private final UtenteService utenteService;

    public AdminStaffController(DipendenteService dipendenteService, DipendenteMapper dipendenteMapper, SedeService sedeService, UtenteService utenteService) {
        this.dipendenteService = dipendenteService;
        this.dipendenteMapper = dipendenteMapper;
        this.sedeService = sedeService;
        this.utenteService = utenteService;
    }

    @GetMapping
    public String showStaffListPage(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        List<DipendenteDto> dipendenti = dipendenteService.getAllDipendenti().stream()
                .map(dipendenteMapper::toDto)
                .filter(d -> !d.getUsername().equals(currentUser.getUsername()))
                .collect(Collectors.toList());
        model.addAttribute("dipendenti", dipendenti);

        if (!model.containsAttribute("dipendenteDto")) {
            model.addAttribute("dipendenteDto", new DipendenteDto());
        }
        model.addAttribute("sedi", sedeService.getAllSedi());
        
        return "admin/staff";
    }

    @PostMapping("/new")
    public String addStaff(@Valid @ModelAttribute("dipendenteDto") DipendenteDto dipendenteDto,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal UserDetails currentUser) {


        if (bindingResult.hasErrors()) {
            List<DipendenteDto> dipendenti = dipendenteService.getAllDipendenti().stream()
                    .map(dipendenteMapper::toDto)
                    .filter(d -> !d.getUsername().equals(currentUser.getUsername()))
                    .collect(Collectors.toList());
            model.addAttribute("dipendenti", dipendenti);
            model.addAttribute("sedi", sedeService.getAllSedi());
            
            return "admin/staff";
        }

        String[] credentials = dipendenteService.registraNuovoDipendente(dipendenteDto);
        
        redirectAttributes.addFlashAttribute("successMessage", 
            "Dipendente creato con successo. Username: " + credentials[0] + " | Password: " + credentials[1]);

        return "redirect:/admin/staff";
    }

    @PostMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        dipendenteService.licenziaDipendente(id);
        redirectAttributes.addFlashAttribute("successMessage", "Dipendente licenziato con successo.");
        return "redirect:/admin/staff";
    }
}
