package com.it342_rentease.it342_rentease_project.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

     @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/")
    public String home() {
        return "home";  // Returns the home page after successful login
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // Show login page (you can omit if not needed)
    }

  @GetMapping("/loginSuccess")
    public String loginSuccess(Model model, OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        model.addAttribute("name", oAuth2User.getAttribute("name"));
        model.addAttribute("email", email);
        model.addAttribute("accessToken", accessToken); // Optional: Only use this securely

        return "loginSuccess";
    }
}

