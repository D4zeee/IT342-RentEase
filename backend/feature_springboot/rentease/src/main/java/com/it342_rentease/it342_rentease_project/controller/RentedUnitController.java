package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.model.RentedUnit;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;
import com.it342_rentease.it342_rentease_project.service.PaymentService;
import com.it342_rentease.it342_rentease_project.service.RentedUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate; // Add this at the top if not already imported
import com.it342_rentease.it342_rentease_project.repository.PaymentReminderRepository;
import com.it342_rentease.it342_rentease_project.model.PaymentReminder;

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
    private PaymentService paymentService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PaymentReminderRepository paymentReminderRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody RentedUnit unit) {
        RentedUnit savedUnit = rentedUnitService.save(unit);

        String amount = String.valueOf((int) (savedUnit.getRoom().getRentalFee()));
        Map<String, Object> paymentIntent = paymentService.createPaymentIntent(amount);

        Map<String, Object> paymentData = (Map<String, Object>) paymentIntent.get("data");
        Map<String, Object> attributes = (Map<String, Object>) paymentData.get("attributes");

        String clientKey = (String) attributes.get("client_key");
        String paymentIntentId = (String) paymentData.get("id");
        String checkoutUrl = (String) attributes.get("checkout_url");

        Map<String, Object> response = new HashMap<>();
        response.put("rentedUnit", savedUnit);
        response.put("clientKey", clientKey);
        response.put("paymentIntentId", paymentIntentId);
        response.put("checkoutUrl", checkoutUrl);
        response.put("roomId", savedUnit.getRoom().getRoomId()); // Add roomId to response

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
        List<RentedUnit> rentedUnits = rentedUnitService.getByRenterId(renterId);
        return new ResponseEntity<>(rentedUnits, HttpStatus.OK);
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
    public ResponseEntity<Map<String, Object>> initiatePayment(@RequestBody Map<String, Long> request) {
        Long roomId = request.get("roomId");
        if (roomId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing roomId"));
        }

        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid roomId"));
        }

        Room room = roomOptional.get();
        String amount = String.valueOf((int) (room.getRentalFee()));
        Map<String, Object> paymentIntent = paymentService.createPaymentIntent(amount);

        Map<String, Object> data = (Map<String, Object>) paymentIntent.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");

        return ResponseEntity.ok(Map.of(
                "paymentIntentId", (String) data.get("id"),
                "clientKey", (String) attributes.get("client_key"),
                "roomId", roomId // Include roomId in the response
        ));

    }

    @GetMapping("/renter/{renterId}/rooms")
    public ResponseEntity<List<Room>> getBookedOrRentedRoomsForRenter(@PathVariable Long renterId) {
        List<RentedUnit> rentedUnits = rentedUnitService.getByRenterId(renterId);
        List<Room> rooms = rentedUnits.stream()
                .map(RentedUnit::getRoom)
                .filter(room -> room.getStatus().equalsIgnoreCase("unavailable")
                        || room.getStatus().equalsIgnoreCase("rented"))
                .collect(java.util.stream.Collectors.toMap(
                    Room::getRoomId, // key: roomId
                    java.util.function.Function.identity(), // value: Room
                    (a, b) -> a // merge function: keep first
                ))
                .values().stream().toList();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/renter/{renterId}/notifications")
    public ResponseEntity<List<RentedUnitNotificationDTO>> getRentedUnitNotificationsByRenter(@PathVariable Long renterId) {
        List<PaymentReminder> reminders = paymentReminderRepository.findByRenterRenterId(renterId);
        List<RentedUnitNotificationDTO> notifications = new java.util.ArrayList<>();
        for (PaymentReminder reminder : reminders) {
            try {
                Room room = reminder.getRoom();
                Long roomId = (room != null) ? room.getRoomId() : null;
                String unitName = (room != null) ? room.getUnitName() : null;
                String note = reminder.getNote();
                String approvalStatus = reminder.getApprovalStatus();
                String startDate = (reminder.getDueDate() != null) ? reminder.getDueDate().toString() : null;

                // Log each notification for debugging
                System.out.println("Notification: roomId=" + roomId + ", unitName=" + unitName + ", note=" + note + ", approvalStatus=" + approvalStatus + ", startDate=" + startDate);

                notifications.add(new RentedUnitNotificationDTO(
                    roomId,
                    unitName,
                    note,
                    approvalStatus,
                    startDate
                ));
            } catch (Exception e) {
                // Log the error and skip this reminder
                System.err.println("Error mapping PaymentReminder to DTO: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // DTO for notification
    class RentedUnitNotificationDTO {
        public Long room_id;
        public String unitname;
        public String note;
        public String approval_status;
        public String startDate;

        public RentedUnitNotificationDTO(Long room_id, String unitname, String note, String approval_status,
                String startDate) {
            this.room_id = room_id;
            this.unitname = unitname;
            this.note = note;
            this.approval_status = approval_status;
            this.startDate = startDate;
        }
    }
}