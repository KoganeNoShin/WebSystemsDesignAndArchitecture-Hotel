package com.websystemdesign.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    // Quando l'utente va sull'indirizzo base (http://localhost:8080/)
    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "<h1>Benvenuto nel sistema Hotel!</h1><p>L'applicazione funziona senza database.</p>";
    }
}
