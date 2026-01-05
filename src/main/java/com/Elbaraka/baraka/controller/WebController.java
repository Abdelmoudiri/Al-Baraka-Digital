package com.Elbaraka.baraka.controller;

import com.Elbaraka.baraka.dto.RegisterRequest;
import com.Elbaraka.baraka.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    private final AuthService authService;

    public WebController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {
        
        // Vérifier que les mots de passe correspondent
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas");
            return "register";
        }

        try {
            RegisterRequest request = new RegisterRequest();
            request.setEmail(email);
            request.setPassword(password);
            request.setFullName(firstName + " " + lastName);
            
            authService.register(request);
            
            model.addAttribute("success", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "client/dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
