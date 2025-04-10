package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.model.Checkin;
import com.it342_rentease.it342_rentease_project.model.Renter;
import com.it342_rentease.it342_rentease_project.repository.CheckinRepository;
import com.it342_rentease.it342_rentease_project.repository.RenterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/checkins")
@CrossOrigin(origins = "http://localhost:3000")
public class CheckinController {

    @Autowired
    private CheckinRepository checkinRepository;

    @Autowired
    private RenterRepository renterRepository;

    @PostMapping
    public ResponseEntity<?> createCheckin(@RequestBody Map<String, String> data) {
        try {
            Long renterId = Long.parseLong(data.get("renterId"));
            Long roomId = Long.parseLong(data.get("id")); // now using "id"
            LocalDate checkinDate = LocalDate.parse(data.get("checkinDate"));
            LocalDate checkoutDate = LocalDate.parse(data.get("checkoutDate"));
    
            Optional<Renter> renterOpt = renterRepository.findById(renterId);
            if (renterOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid renter ID");
            }
    
            Checkin checkin = new Checkin();
            checkin.setRenter(renterOpt.get());
            checkin.setId(roomId); // renamed from setRoomId
            checkin.setCheckinDate(checkinDate);
            checkin.setCheckoutDate(checkoutDate);
    
            return ResponseEntity.ok(checkinRepository.save(checkin));
    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    
    
}
