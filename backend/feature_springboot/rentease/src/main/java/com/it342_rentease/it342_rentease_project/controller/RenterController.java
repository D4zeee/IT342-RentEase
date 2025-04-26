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
@CrossOrigin(origins = "*") // Allow all origins temporarily for mobile testing
public class RenterController {

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private RenterRepository renterRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> registerRenter(@RequestBody Renter renter) {
        try {
            if (renterRepository.existsByEmail(renter.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
            }

            renter.setPassword(passwordEncoder.encode(renter.getPassword()));
            Renter savedRenter = renterRepository.save(renter);
            return ResponseEntity.ok(savedRenter);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRenters() {
        return ResponseEntity.ok(renterRepository.findAll());
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentRenter(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        if (email == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
        }

        Optional<Renter> renterOptional = renterRepository.findByEmail(email);
        if (renterOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Renter not found");
        }

        Renter renter = renterOptional.get();

        // ✅ This is crucial
        Map<String, Object> response = new HashMap<>();
        response.put("name", renter.getName()); // ✅ make sure key is "name"
        response.put("email", renter.getEmail());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-name")
    public ResponseEntity<?> updateName(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token); // Make sure subject is email

        Optional<Renter> renterOptional = renterRepository.findByEmail(email);
        if (renterOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        Renter renter = renterOptional.get();
        String newName = request.get("name");
        renter.setName(newName);
        renterRepository.save(renter);

        return ResponseEntity.ok(Map.of("message", "Name updated"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRenter(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        Optional<Renter> renterOptional = renterRepository.findByEmail(email);
        if (renterOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Renter not found");
        }

        renterRepository.delete(renterOptional.get());
        return ResponseEntity.ok(Map.of("message", "Renter deleted successfully"));
    }
}
