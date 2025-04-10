package com.it342_rentease.it342_rentease_project.service;

import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

// RoomService.java
public Optional<Room> getRoomById(Long roomId) {
    return roomRepository.findById(roomId);
}

public Room updateRoom(Long roomId, Room room) {
    Optional<Room> existingRoom = roomRepository.findById(roomId);
    if (existingRoom.isPresent()) {
        Room updatedRoom = existingRoom.get();
        updatedRoom.setOwnerId(room.getOwnerId());
        updatedRoom.setUnitName(room.getUnitName());
        updatedRoom.setNumberOfRooms(room.getNumberOfRooms());
        updatedRoom.setDescription(room.getDescription());
        updatedRoom.setRentalFee(room.getRentalFee());
        updatedRoom.setAddressLine1(room.getAddressLine1());
        updatedRoom.setAddressLine2(room.getAddressLine2());
        updatedRoom.setCity(room.getCity());
        updatedRoom.setPostalCode(room.getPostalCode());
        return roomRepository.save(updatedRoom);
    }
    return null;
}

public boolean deleteRoom(Long roomId) {
    try {
        roomRepository.deleteById(roomId);
        return true;
    } catch (Exception e) {
        return false;
    }
}
}
