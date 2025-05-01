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

    @PostMapping("/register")
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

            // Generate JWT with renterId and renterName
            String token = jwtUtil.generateTokenForRenter(renter.getEmail(), renter.getRenterId(), renter.getName());

            Map<String, String> response = new HashMap<>();
            response.put("jwt", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRenters() {
        return ResponseEntity.ok(renterRepository.findAll());
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentRenter(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        Long renterId = jwtUtil.extractRenterId(token);
        String renterName = jwtUtil.extractRenterName(token);

        // Optional: Still query the database to ensure data is fresh
        Optional<Renter> renterOptional = renterRepository.findByEmail(email);
        if (renterOptional.isPresent()) {
            Renter renter = renterOptional.get();
            Map<String, Object> response = new HashMap<>();
            response.put("email", renter.getEmail());
            response.put("renterId", renter.getRenterId());
            response.put("name", renter.getName());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Renter not found");
        }
    }

    @PatchMapping("/update-name")
    public ResponseEntity<?> updateRenterName(@RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> updateData) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);

        Optional<Renter> renterOptional = renterRepository.findByEmail(email);
        if (renterOptional.isPresent()) {
            Renter renter = renterOptional.get();
            String newName = updateData.get("name");
            if (newName != null && !newName.isEmpty()) {
                renter.setName(newName);
                renterRepository.save(renter);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Name updated successfully");
                response.put("newName", renter.getName());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Name must not be empty");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Renter not found");
        }
    }

}
