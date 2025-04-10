package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.dto.*;
import com.it342_rentease.it342_rentease_project.model.Owner;
import com.it342_rentease.it342_rentease_project.repository.OwnerRepository;
import com.it342_rentease.it342_rentease_project.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owners")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;


    @GetMapping
    public ResponseEntity<?> getAllOwners() {
        return ResponseEntity.ok(ownerRepository.findAll());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (ownerRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }
        Owner owner = new Owner();
        owner.setUsername(request.getUsername());
        owner.setPassword(passwordEncoder.encode(request.getPassword()));
        ownerRepository.save(owner);
        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtUtils.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
