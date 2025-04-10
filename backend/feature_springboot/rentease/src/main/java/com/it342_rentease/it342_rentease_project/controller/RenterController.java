package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.model.Renter;
import com.it342_rentease.it342_rentease_project.repository.RenterRepository;
import com.it342_rentease.it342_rentease_project.security.JwtUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/renters")
@CrossOrigin(origins = "http://localhost:3000")
public class RenterController {

    @Autowired
    private JwtUtils jwtUtil;


    @Autowired
    private RenterRepository renterRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping
    public Renter registerRenter(@RequestBody Renter renter) {
        if (renterRepository.existsByEmail(renter.getEmail())) {
            throw new RuntimeException("Email already in use.");
        }

        // Encrypt password
        renter.setPassword(passwordEncoder.encode(renter.getPassword()));

        return renterRepository.save(renter);
    }
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
    try {
        String email = loginData.get("email");
        String password = loginData.get("password");

        Optional<Renter> renterOptional = renterRepository.findByEmail(email);
        if (renterOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        Renter renter = renterOptional.get();
        if (!passwordEncoder.matches(password, renter.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        String token = jwtUtil.generateTokenForRenter(renter.getEmail());


        Map<String, String> response = new HashMap<>();
        response.put("jwt", token);
    

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        e.printStackTrace(); // For terminal logs
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(Map.of("error", e.getMessage()));
    }
}

@GetMapping
public ResponseEntity<?> getAllRenters() {
    return ResponseEntity.ok(renterRepository.findAll());
}


}
