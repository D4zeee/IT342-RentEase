package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.model.RentedUnit;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;
import com.it342_rentease.it342_rentease_project.service.PaymentService;
import com.it342_rentease.it342_rentease_project.service.RentedUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rented_units")
@CrossOrigin(origins = "http://localhost:5173")
public class RentedUnitController {

    @Autowired
    private RentedUnitService rentedUnitService;

    @Autowired
    private PaymentService paymentService; // 🚀 Inject PaymentService

    @Autowired
    private RoomRepository roomRepository;

     @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody RentedUnit unit) {
        RentedUnit savedUnit = rentedUnitService.save(unit);

        // 🚀 Create Payment Intent based on Room's Rental Fee
        String amount = String.valueOf((int)(savedUnit.getRoom().getRentalFee())); // PayMongo expects amount in **centavos**
        Map<String, Object> paymentIntent = paymentService.createPaymentIntent(amount);

        // 🚀 Get checkout URL from PayMongo response

        Map<String, Object> paymentData = (Map<String, Object>) paymentIntent.get("data");
        Map<String, Object> attributes = (Map<String, Object>) paymentData.get("attributes");

        String clientKey = (String) attributes.get("client_key"); // 👈 You forgot to get this
        String paymentIntentId = (String) paymentData.get("id"); // ✅ now you define it
        String checkoutUrl = (String) attributes.get("checkout_url");

        
        // 🚀 Return the payment link to frontend
        Map<String, Object> response = new HashMap<>();
        response.put("rentedUnit", savedUnit);
        response.put("clientKey", clientKey);
        response.put("paymentIntentId", paymentIntentId); // 👈 add this
        response.put("checkoutUrl", checkoutUrl);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RentedUnit>> getAll() {
        return new ResponseEntity<>(rentedUnitService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentedUnit> getById(@PathVariable Long id) {
        Optional<RentedUnit> unit = rentedUnitService.getById(id);
        return unit.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/renter/{renterId}")
    public ResponseEntity<List<RentedUnit>> getByRenter(@PathVariable Long renterId) {
        return new ResponseEntity<>(rentedUnitService.getByRenterId(renterId), HttpStatus.OK);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<RentedUnit>> getByRoom(@PathVariable Long roomId) {
        return new ResponseEntity<>(rentedUnitService.getByRoomId(roomId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentedUnitService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/initiate-payment")
public ResponseEntity<Map<String, String>> initiatePayment(@RequestBody Map<String, Long> request) {
    Long roomId = request.get("roomId");
    if (roomId == null) {
        return ResponseEntity.badRequest().body(Map.of("error", "Missing roomId"));
    }

    Optional<Room> roomOptional = roomRepository.findById(roomId);
    if (roomOptional.isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid roomId"));
    }

    Room room = roomOptional.get();
    String amount = String.valueOf((int)(room.getRentalFee()));
    Map<String, Object> paymentIntent = paymentService.createPaymentIntent(amount);

    Map<String, Object> data = (Map<String, Object>) paymentIntent.get("data");
    Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");

    return ResponseEntity.ok(Map.of(
        "paymentIntentId", (String) data.get("id"),
        "clientKey", (String) attributes.get("client_key")
    ));
}

}