package com.websystemdesign.controller.web;

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
        // Passiamo un oggetto Utente vuoto al form, necessario per la validazione
        model.addAttribute("utente", new Utente());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("utente") Utente utente,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {

        // Se ci sono errori di validazione, torna al form di registrazione
        if (bindingResult.hasErrors()) {
            return "register"; // La vista mostrerà gli errori
        }

        // (Opzionale ma consigliato) Controlla se l'username esiste già
        if (utenteService.getUtenteByUsername(utente.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.utente", "Questo username è già stato preso.");
            return "register";
        }

        // TODO: Criptare la password prima di salvarla!
        utenteService.saveUtente(utente);

        // Passa un messaggio di successo alla pagina di login
        redirectAttributes.addFlashAttribute("successMessage", "Registrazione completata! Ora puoi accedere.");
        return "redirect:/login";
    }
}
