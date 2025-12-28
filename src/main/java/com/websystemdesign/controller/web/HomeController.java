package com.websystemdesign.controller.web;

import com.websystemdesign.service.CameraService;
import com.websystemdesign.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    private final CameraService cameraService;
    private final SedeService sedeService;

    @Autowired
    public HomeController(CameraService cameraService, SedeService sedeService) {
        this.cameraService = cameraService;
        this.sedeService = sedeService;
    }

    // Reindirizza la radice del sito verso /home
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home() {
        return "index";
    }

    @GetMapping("/home/rooms")
    public String showRooms(Model model) {
        model.addAttribute("camere", cameraService.getAllCamera());
        model.addAttribute("sedi", sedeService.getAllSedi()); // Aggiungo le sedi per il filtro
        return "rooms";
    }

    @GetMapping("/home/contact")
    public String showContact(Model model) {
        model.addAttribute("sedi", sedeService.getAllSedi());
        return "contact";
    }
}
