package com.it342_rentease.it342_rentease_project.service;

import com.it342_rentease.it342_rentease_project.model.Payment;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

@Service
public class PaymentService {

    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());

    @Value("${paymongo.secret_key}")
    private String PAYMONGO_SECRET_KEY;

    @Value("${paymongo.intent_url}")
    private String PAYMONGO_INTENT_URL;

    @Value("${paymongo.method_url}")
    private String PAYMONGO_METHOD_URL;

    @Autowired
    private PaymentRepository paymentRepository;

    private HttpHeaders createPayMongoHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuth = Base64.getEncoder().encodeToString((PAYMONGO_SECRET_KEY + ":").getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public Map<String, Object> createPaymentIntent(String amount) {
        if (PAYMONGO_SECRET_KEY == null || PAYMONGO_SECRET_KEY.isEmpty() ||
            PAYMONGO_INTENT_URL == null || PAYMONGO_INTENT_URL.isEmpty()) {
            throw new IllegalArgumentException("PayMongo ENV values are not set.");
        }
    
        RestTemplate restTemplate = new RestTemplate(); 
        HttpHeaders headers = createPayMongoHeaders();
    
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("amount", Integer.parseInt(amount) * 100);
        attributes.put("currency", "PHP");
        attributes.put("payment_method_allowed", List.of("gcash"));
        attributes.put("description", "RentEase Payment");
        attributes.put("statement_descriptor", "RentEase");
    
        Map<String, Object> data = Map.of("attributes", attributes);
        Map<String, Object> requestBody = Map.of("data", data);
    
        System.out.println("=== Sending PayMongo PaymentIntent ===");
        System.out.println(requestBody);
    
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            PAYMONGO_INTENT_URL,
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<>() {}
        );
    
        return response.getBody();
    }

    public Map<String, Object> createPaymentMethod(String name, String email, String phone, String type) {
        if (PAYMONGO_SECRET_KEY == null || PAYMONGO_SECRET_KEY.isEmpty() ||
            PAYMONGO_METHOD_URL == null || PAYMONGO_METHOD_URL.isEmpty()) {
            throw new IllegalArgumentException("PayMongo ENV values are not set.");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createPayMongoHeaders();

        Map<String, Object> billing = Map.of(
            "name", name,
            "email", email,
            "phone", phone
        );

        Map<String, Object> attributes = Map.of(
            "billing", billing,
            "type", type
        );

        Map<String, Object> data = Map.of("attributes", attributes);
        Map<String, Object> requestBody = Map.of("data", data);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            PAYMONGO_METHOD_URL,
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    public Map<String, Object> attachPaymentIntent(String intentId, String paymentMethod, String clientKey, String returnUrl) {
        if (PAYMONGO_SECRET_KEY == null || PAYMONGO_SECRET_KEY.isEmpty() ||
            PAYMONGO_INTENT_URL == null || PAYMONGO_INTENT_URL.isEmpty()) {
            throw new IllegalArgumentException("PayMongo ENV values are not set.");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createPayMongoHeaders();

        Map<String, Object> attributes = Map.of(
            "payment_method", paymentMethod,
            "client_key", clientKey,
            "return_url", returnUrl
        );

        Map<String, Object> data = Map.of("attributes", attributes);
        Map<String, Object> requestBody = Map.of("data", data);

        String attachUrl = PAYMONGO_INTENT_URL + "/" + intentId + "/attach";
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            attachUrl,
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    public Map<String, Object> retrievePaymentIntent(String intentId) {
        if (PAYMONGO_SECRET_KEY == null || PAYMONGO_SECRET_KEY.isEmpty() ||
            PAYMONGO_INTENT_URL == null || PAYMONGO_INTENT_URL.isEmpty()) {
            logger.severe("PayMongo ENV values are not set.");
            throw new IllegalArgumentException("PayMongo ENV values are not set.");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createPayMongoHeaders();

        String retrieveUrl = PAYMONGO_INTENT_URL + "/" + intentId;
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            logger.info("Retrieving payment intent from PayMongo: " + retrieveUrl);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                retrieveUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
            );
            logger.info("Successfully retrieved payment intent: " + response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.severe("Failed to retrieve payment intent from PayMongo: " + e.getMessage());
            throw e;
        }
    }

    public Payment savePayment(String paymentIntentId, Room room) {
        Map<String, Object> paymentIntent;
        try {
            paymentIntent = retrievePaymentIntent(paymentIntentId);
        } catch (Exception e) {
            logger.severe("Failed to retrieve payment intent during save: " + e.getMessage());
            // Fallback: Save with default values
            Payment payment = new Payment();
            payment.setPaymentIntentId(paymentIntentId);
            payment.setAmount(0.0f); // Default amount
            payment.setStatus("Pending");
            payment.setPaymentMethod("gcash");
            payment.setPaidDate(null); // Set to null for failed retrieval
            payment.setRoom(room);
            return paymentRepository.save(payment);
        }

        Map<String, Object> data = (Map<String, Object>) paymentIntent.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");

        String status = (String) attributes.get("status");
        Integer amount = (Integer) attributes.get("amount");
        List<String> paymentMethods = (List<String>) attributes.get("payment_method_allowed");
        String paymentMethod = paymentMethods != null && !paymentMethods.isEmpty() ? paymentMethods.get(0) : "gcash";

        Payment payment = new Payment();
        payment.setPaymentIntentId(paymentIntentId);
        payment.setAmount(amount / 100.0f);
        payment.setStatus(status.equals("succeeded") ? "Paid" : "Pending");
        payment.setPaymentMethod(paymentMethod);
        payment.setPaidDate(status.equals("succeeded") ? LocalDate.now() : null); // Set paid_date if status is succeeded
        payment.setRoom(room);

        return paymentRepository.save(payment);
    }
}