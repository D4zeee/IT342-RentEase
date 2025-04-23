package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.model.PaymentReminder;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.RentedUnitRepository;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;
import com.it342_rentease.it342_rentease_project.service.PaymentReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payment_reminders")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentReminderController {

    @Autowired
    private PaymentReminderService paymentReminderService;

    @Autowired
    private RentedUnitRepository rentedUnitRepository;

    @Autowired
    private RoomRepository roomRepository;

@PostMapping
public ResponseEntity<?> create(@RequestBody PaymentReminder reminder) {
    // ✅ Fetch the full Room object from DB using roomId
    Long roomId = reminder.getRoom().getRoomId();
    Room room = roomRepository.findById(roomId).orElse(null);
    if (room == null || room.getOwner() == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid room or room has no owner.");
    }
    Long roomOwnerId = room.getOwner().getOwnerId();
    Long selectedOwnerId = reminder.getOwner().getOwnerId();
    if (!roomOwnerId.equals(selectedOwnerId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only set reminders for your own rooms.");
    }
    // ✅ Inject the fully fetched room into the reminder
    reminder.setRoom(room);
    return new ResponseEntity<>(paymentReminderService.create(reminder), HttpStatus.CREATED);
}

    @GetMapping
    public ResponseEntity<List<PaymentReminder>> getAll() {
        return new ResponseEntity<>(paymentReminderService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentReminder> getById(@PathVariable Long id) {
        Optional<PaymentReminder> reminder = paymentReminderService.getById(id);
        return reminder.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/renter/{renterId}")
    public ResponseEntity<List<PaymentReminder>> getByRenterId(@PathVariable Long renterId) {
        return new ResponseEntity<>(paymentReminderService.getByRenterId(renterId), HttpStatus.OK);
    }

    @GetMapping("/owner/{ownerId}")
public ResponseEntity<List<PaymentReminder>> getByOwnerId(@PathVariable Long ownerId) {
    List<PaymentReminder> all = paymentReminderService.getByOwnerId(ownerId);
    
    // Filter only reminders for rooms that the owner actually owns
    List<PaymentReminder> filtered = all.stream()
        .filter(reminder -> reminder.getRoom().getOwner().getOwnerId().equals(ownerId))
        .toList();

    return new ResponseEntity<>(filtered, HttpStatus.OK);
}


    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<PaymentReminder>> getByRoomId(@PathVariable Long roomId) {
        return new ResponseEntity<>(paymentReminderService.getByRoomId(roomId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentReminderService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
