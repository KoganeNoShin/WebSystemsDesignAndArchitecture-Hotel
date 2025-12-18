package com.websystemdesign.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    @GetMapping("/dashboard")
    public String showStaffDashboard() {
        return "staff/dashboard"; // Cerca in templates/staff/dashboard.html
    }
}
