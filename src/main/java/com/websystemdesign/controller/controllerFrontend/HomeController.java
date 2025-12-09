package com.websystemdesign.controller.controllerFrontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Quando l'utente va sull'indirizzo base (http://localhost:8080/)
    @GetMapping("/")
    public String home() {
        // Restituisce il nome del file HTML che si trova in src/main/resources/templates
        return "index";
    }
}
