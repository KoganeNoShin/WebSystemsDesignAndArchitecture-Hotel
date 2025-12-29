package com.websystemdesign.controller.web;

import com.websystemdesign.dto.RegistrationDto;
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

    @Autowired
    public AuthController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {

        // Controlla se l'username esiste già
        if (utenteService.getUtenteByUsername(registrationDto.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.registrationDto", "Questo username è già stato preso.");
        }

        // Se ci sono già errori di validazione base, non procedere oltre
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Prova a registrare l'utente
        try {
            utenteService.registraNuovoCliente(registrationDto);
        } catch (Exception e) {
            // Gestione per altri errori imprevisti
            bindingResult.reject("error.global", "Si è verificato un errore durante la registrazione. Riprova.");
            return "register";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Registrazione completata! Ora puoi accedere.");
        return "redirect:/login";
    }
}
