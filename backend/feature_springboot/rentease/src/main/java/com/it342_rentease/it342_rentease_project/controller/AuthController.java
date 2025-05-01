package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.dto.*;
import com.it342_rentease.it342_rentease_project.model.Owner;
import com.it342_rentease.it342_rentease_project.repository.OwnerRepository;
import com.it342_rentease.it342_rentease_project.security.JwtUtils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @CrossOrigin(origins = "http://localhost:5173") 
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

@GetMapping("/current-user")
public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");
    
    // Validate the token using your JWT utility
    String username = jwtUtils.extractUsername(token);
    
    if (username != null) {
        // Find the Owner by the username
        Owner owner = ownerRepository.findByUsername(username).orElse(null);
        
        if (owner != null) {
            // Return both username and ownerId
            Map<String, Object> response = new HashMap<>();
            response.put("username", owner.getUsername());
            response.put("ownerId", owner.getOwnerId());
            
            return ResponseEntity.ok(response); // Return username and ownerId as a JSON object
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Owner not found");
        }
    } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
    }
}


    @DeleteMapping("/{ownerId}")
    public ResponseEntity<?> deleteOwner(@PathVariable Long ownerId) {
        if (!ownerRepository.existsById(ownerId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Owner not found");
        }
    
        ownerRepository.deleteById(ownerId); // This will also delete rooms because of cascade
        return ResponseEntity.noContent().build();
    }
    

}