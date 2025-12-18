package com.websystemdesign.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteDashboardController {

    @GetMapping("/dashboard")
    public String showClienteDashboard() {
        return "cliente/dashboard"; // Cerca in templates/cliente/dashboard.html
    }
}
