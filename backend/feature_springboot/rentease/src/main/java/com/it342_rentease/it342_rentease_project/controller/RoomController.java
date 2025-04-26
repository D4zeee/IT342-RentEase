package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseEntity<Room> createRoom(
            @RequestPart("room") Room room,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        try {
            Room createdRoom = roomService.createRoom(room, images);
            return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Optional<Room> room = roomService.getRoomById(id);
        return room.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable Long id,
            @RequestPart("room") Room room,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        try {
            Room updatedRoom = roomService.updateRoom(id, room, images);
            if (updatedRoom != null) {
                return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        boolean deleted = roomService.deleteRoom(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Room>> getRoomsByOwnerId(@PathVariable Long ownerId) {
        List<Room> rooms = roomService.getRoomsByOwnerId(ownerId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/owner/{ownerId}/unavailable")
    public ResponseEntity<List<Room>> getUnavailableRoomsByOwnerId(@PathVariable Long ownerId) {
    List<Room> unavailableRooms = roomService.getUnavailableRoomsByOwnerId(ownerId);
    return new ResponseEntity<>(unavailableRooms, HttpStatus.OK);
    }

}