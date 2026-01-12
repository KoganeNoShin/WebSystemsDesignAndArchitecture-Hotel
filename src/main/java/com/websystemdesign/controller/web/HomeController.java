package com.websystemdesign.controller.web;

import com.websystemdesign.service.CameraService;
import com.websystemdesign.service.SedeService;
import com.websystemdesign.service.ServiceHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CameraService cameraService;
    private final SedeService sedeService;
    private final ServiceHotelService serviceHotelService;

    @Autowired
    public HomeController(CameraService cameraService, SedeService sedeService, ServiceHotelService serviceHotelService) {
        this.cameraService = cameraService;
        this.sedeService = sedeService;
        this.serviceHotelService = serviceHotelService;
    }

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
        model.addAttribute("sedi", sedeService.getAllSedi());
        return "rooms";
    }

    @GetMapping("/home/contact")
    public String showContact(Model model) {
        model.addAttribute("sedi", sedeService.getAllSedi());
        return "contact";
    }

    @GetMapping("/home/services")
    public String showServices(Model model) {
        model.addAttribute("servizi", serviceHotelService.getAllServices());
        model.addAttribute("sedi", sedeService.getAllSedi());
        return "services";
    }
}
