package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.model.Payment;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;
import com.it342_rentease.it342_rentease_project.security.JwtUtils;
import com.it342_rentease.it342_rentease_project.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private static final Logger logger = Logger.getLogger(PaymentController.class.getName());

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RoomRepository roomRepository;

   


    @PostMapping("/intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, String> request) {
        String amount = request.get("amount");

        if (amount == null || !amount.matches("\\d+")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or missing amount"));
        }

        Map<String, Object> response = paymentService.createPaymentIntent(amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/method")
    public ResponseEntity<Map<String, Object>> createPaymentMethod(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String phone = request.get("phone");
        String type = request.get("type");

        if (name == null || email == null || phone == null || type == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }

        Map<String, Object> response = paymentService.createPaymentMethod(name, email, phone, type);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/intent/attach/{id}")
    public ResponseEntity<Map<String, Object>> attachPaymentIntent(
            @PathVariable("id") String intentId,
            @RequestBody Map<String, String> request) {
        String paymentMethod = request.get("payment_method");
        String clientKey = request.get("client_key");
        String returnUrl = request.get("return_url");

        if (paymentMethod == null || clientKey == null || returnUrl == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }

        Map<String, Object> response = paymentService.attachPaymentIntent(intentId, paymentMethod, clientKey, returnUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/intent/{id}")
    public ResponseEntity<Map<String, Object>> retrievePaymentIntent(
            @PathVariable("id") String intentId,
            @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to retrieve payment intent: " + intentId);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warning("Missing or invalid token for retrievePaymentIntent: " + authHeader);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing or invalid token"));
        }

        try {
            Map<String, Object> response = paymentService.retrievePaymentIntent(intentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error retrieving payment intent: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve payment intent: " + e.getMessage()));
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> savePayment(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        logger.info("Received request to save payment: " + request);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warning("Missing or invalid token for savePayment: " + authHeader);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing or invalid token"));
        }

        String paymentIntentId = (String) request.get("paymentIntentId");
        Long roomId;
        try {
            roomId = Long.valueOf(request.get("roomId").toString());
        } catch (NumberFormatException e) {
            logger.warning("Invalid roomId format: " + request.get("roomId"));
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid roomId format"));
        }

        if (paymentIntentId == null || roomId == null) {
            logger.warning("Missing paymentIntentId or roomId: " + request);
            return ResponseEntity.badRequest().body(Map.of("error", "Missing paymentIntentId or roomId"));
        }

        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            logger.warning("Invalid roomId: " + roomId);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid roomId"));
        }

        Room room = roomOptional.get();
        try {
            Payment savedPayment = paymentService.savePayment(paymentIntentId, room);
            logger.info("Payment saved successfully: " + savedPayment.getPaymentId());
            return ResponseEntity.ok(Map.of(
                "paymentId", savedPayment.getPaymentId(),
                "status", savedPayment.getStatus(),
                "amount", savedPayment.getAmount(),
                "paymentMethod", savedPayment.getPaymentMethod(),
                "paymentIntentId", savedPayment.getPaymentIntentId()
            ));
        } catch (ClassCastException e) {
            logger.severe("ClassCastException while saving payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save payment: Invalid data format from PayMongo API"));
        } catch (Exception e) {
            logger.severe("Failed to save payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save payment: " + e.getMessage()));
        }
    }

    @GetMapping("/by-intent-id/{paymentIntentId}")
public ResponseEntity<?> getPaymentByIntentId(@PathVariable String paymentIntentId,
                                              @RequestHeader("Authorization") String authHeader) {
    

    Optional<Payment> payment = paymentService.getPaymentByIntentId(paymentIntentId);
    if (payment.isPresent()) {
        return ResponseEntity.ok(payment.get());
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Payment not found"));
    }
}





}