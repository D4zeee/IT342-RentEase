package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.model.Payment;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.PaymentRepository;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;
import com.it342_rentease.it342_rentease_project.service.RoomService;
import com.nimbusds.jose.util.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
@CrossOrigin(origins = "http://localhost:5173")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping
    public ResponseEntity<Room> createRoom(
            @RequestPart("room") Room room,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            System.out.println("Received POST request to add a room");
            System.out.println("Room data: " + room);
            if (images != null) {
                System.out.println("Number of images received: " + images.size());
                for (int i = 0; i < images.size(); i++) {
                    System.out.println("Image " + i + ": " + images.get(i).getOriginalFilename());
                }
            } else {
                System.out.println("No images received");
            }

            Room createdRoom = roomService.createRoom(room, images);
            System.out.println("Room created with ID: " + createdRoom.getRoomId());
            return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
        } catch (IOException e) {
            System.err.println("Error adding room: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        try {
            System.out.println("Received GET request to fetch all rooms");
            List<Room> rooms = roomService.getAllRooms();
            if (rooms.isEmpty()) {
                System.out.println("No rooms found");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            System.out.println("Returning " + rooms.size() + " rooms");
            return new ResponseEntity<>(rooms, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching all rooms: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/owner/{ownerId}/unavailable")
    public ResponseEntity<List<Room>> getUnavailableRoomsByOwner(@PathVariable Long ownerId) {
        try {
            System.out.println("Received GET request for unavailable rooms for owner ID: " + ownerId);
            List<Room> rooms = roomService.getUnavailableRoomsByOwnerId(ownerId);
            System.out.println("Returning " + rooms.size() + " unavailable rooms");
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            System.err.println("Error fetching unavailable rooms for owner ID " + ownerId + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable("roomId") Long roomId) {
        try {
            System.out.println("Received GET request for room ID: " + roomId);
            Optional<Room> roomData = roomService.getRoomById(roomId);
            if (roomData.isPresent()) {
                System.out.println("Room found with ID: " + roomId);
                return new ResponseEntity<>(roomData.get(), HttpStatus.OK);
            } else {
                System.out.println("Room not found with ID: " + roomId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error fetching room with ID " + roomId + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable("roomId") Long roomId,
            @RequestPart("room") Room room,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "removedImages", required = false) String removedImagesJson) {
        try {
            System.out.println("Received PUT request to update room ID: " + roomId);
            System.out.println("Room data: " + room);
            if (images != null) {
                System.out.println("Number of images received: " + images.size());
                for (int i = 0; i < images.size(); i++) {
                    System.out.println("Image " + i + ": " + images.get(i).getOriginalFilename());
                }
            } else {
                System.out.println("No images received");
            }
            if (removedImagesJson != null) {
                System.out.println("Removed images JSON: " + removedImagesJson);
            }

            Room updatedRoom = roomService.updateRoom(roomId, room, images, removedImagesJson);
            if (updatedRoom != null) {
                System.out.println("Room updated with ID: " + roomId);
                return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
            } else {
                System.out.println("Room not found with ID: " + roomId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            System.err.println("Error updating room with ID " + roomId + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<HttpStatus> deleteRoom(@PathVariable("roomId") Long roomId) {
        try {
            System.out.println("Received DELETE request for room ID: " + roomId);
            if (roomService.deleteRoom(roomId)) {
                System.out.println("Room deleted with ID: " + roomId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                System.out.println("Room not found with ID: " + roomId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error deleting room with ID " + roomId + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Room>> getRoomsByOwnerId(@PathVariable("ownerId") Long ownerId) {
        try {
            System.out.println("Received GET request for rooms by owner ID: " + ownerId);
            List<Room> rooms = roomService.getRoomsByOwnerId(ownerId);
            if (rooms.isEmpty()) {
                System.out.println("No rooms found for owner ID: " + ownerId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            System.out.println("Returning " + rooms.size() + " rooms for owner ID: " + ownerId);
            return new ResponseEntity<>(rooms, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching rooms for owner ID " + ownerId + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

// Updated room-stats endpoint
@GetMapping("/owner/{ownerId}/room-stats")
public ResponseEntity<Map<String, Object>> getRoomStatsByOwner(@PathVariable Long ownerId) {
    try {
        // Count total, available, and rented rooms
        long total = roomRepository.countByOwnerOwnerId(ownerId);
        long available = roomRepository.countByOwnerOwnerIdAndStatus(ownerId, "available");
        long rented = roomRepository.countByOwnerOwnerIdAndStatus(ownerId, "rented");

        // Calculate revenue from paid payments for rooms owned by ownerId
        List<Payment> paidPayments = paymentRepository.findByRoomOwnerOwnerIdAndStatus(ownerId, "Paid");
        double revenue = paidPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("available", available);
        stats.put("rented", rented);
        stats.put("revenue", revenue);

        return ResponseEntity.ok(stats);
    } catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    @PatchMapping("/{roomId}/status")
    public ResponseEntity<?> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestBody Map<String, String> requestBody) {
        try {
            String newStatus = requestBody.get("status");
            Optional<Room> optionalRoom = roomRepository.findById(roomId);

            if (optionalRoom.isEmpty()) {
                return new ResponseEntity<>("Room not found", HttpStatus.NOT_FOUND);
            }

            Room room = optionalRoom.get();
            room.setStatus(newStatus);
            roomRepository.save(room);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update room status: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}