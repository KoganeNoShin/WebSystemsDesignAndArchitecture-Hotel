package com.websystemdesign.controller.web;

import com.websystemdesign.dto.UtenteDto;
import com.websystemdesign.mapper.UtenteMapper;
import com.websystemdesign.model.Utente;
import com.websystemdesign.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UtenteService utenteService;
    private final UtenteMapper utenteMapper;

    @Autowired
    public AuthController(UtenteService utenteService, UtenteMapper utenteMapper) {
        this.utenteService = utenteService;
        this.utenteMapper = utenteMapper;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // Passiamo un DTO vuoto al form, non più l'entità
        model.addAttribute("utenteDto", new UtenteDto());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("utenteDto") UtenteDto utenteDto,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {

        // Se ci sono errori di validazione, torna al form di registrazione
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Controlla se l'username esiste già
        if (utenteService.getUtenteByUsername(utenteDto.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.utente", "Questo username è già stato preso.");
            return "register";
        }

        // Convertiamo il DTO in Entità prima di salvarlo
        Utente nuovoUtente = utenteMapper.toEntity(utenteDto);

        // TODO: Criptare la password prima di salvarla!
        utenteService.saveUtente(nuovoUtente);

        redirectAttributes.addFlashAttribute("successMessage", "Registrazione completata! Ora puoi accedere.");
        return "redirect:/login";
    }
}
