package com.varahiedits.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/home")
    public String home() {
        return "varahi-edits";   // home page
    }

    @GetMapping("/admin")
    public String adminLogin() {
        return "varahi-admin-login";   // login page
    }

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "varahi-admin-dashboard";   // dashboard page
    }
}