package com.it342_rentease.it342_rentease_project.service;

import com.it342_rentease.it342_rentease_project.model.Owner;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.OwnerRepository;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    private final Path uploadDir = Paths.get("uploads");

    public RoomService() {
        try {
            Files.createDirectories(uploadDir);
            System.out.println("Upload directory created at: " + uploadDir.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Transactional
    public Room createRoom(Room room, List<MultipartFile> images) throws IOException {
        System.out.println("Creating room with data: " + room);
        if (room.getOwner() != null && room.getOwner().getOwnerId() != null) {
            Optional<Owner> owner = ownerRepository.findById(room.getOwner().getOwnerId());
            if (owner.isPresent()) {
                room.setOwner(owner.get());
            } else {
                throw new IllegalArgumentException("Owner with ID " + room.getOwner().getOwnerId() + " not found");
            }
        } else {
            throw new IllegalArgumentException("Owner must be specified for the Room");
        }

        List<String> imagePaths = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            System.out.println("Processing " + images.size() + " images");
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                    Path filePath = uploadDir.resolve(fileName);
                    System.out.println("Saving image to: " + filePath);
                    try {
                        Files.write(filePath, image.getBytes());
                        System.out.println("Image saved successfully to: " + filePath);
                    } catch (IOException e) {
                        System.err.println("Failed to save image to " + filePath + ": " + e.getMessage());
                        throw e;
                    }
                    imagePaths.add(filePath.toString());
                } else {
                    System.out.println("Skipping empty image file");
                }
            }
        } else {
            System.out.println("No images provided");
        }
        room.setImagePaths(imagePaths);
        System.out.println("Image paths to save: " + imagePaths);

        Room savedRoom = roomRepository.save(room);
        System.out.println("Room saved with ID: " + savedRoom.getRoomId());
        return savedRoom;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long roomId) {
        return roomRepository.findById(roomId);
    }

    @Transactional
    public Room updateRoom(Long roomId, Room room, List<MultipartFile> images) throws IOException {
        System.out.println("Updating room with ID: " + roomId);
        Optional<Room> existingRoom = roomRepository.findById(roomId);
        if (existingRoom.isPresent()) {
            Room updatedRoom = existingRoom.get();
            if (room.getOwner() != null && room.getOwner().getOwnerId() != null) {
                Optional<Owner> owner = ownerRepository.findById(room.getOwner().getOwnerId());
                if (owner.isPresent()) {
                    updatedRoom.setOwner(owner.get());
                } else {
                    throw new IllegalArgumentException("Owner with ID " + room.getOwner().getOwnerId() + " not found");
                }
            }
            updatedRoom.setUnitName(room.getUnitName());
            updatedRoom.setNumberOfRooms(room.getNumberOfRooms());
            updatedRoom.setDescription(room.getDescription());
            updatedRoom.setRentalFee(room.getRentalFee());
            updatedRoom.setAddressLine1(room.getAddressLine1());
            updatedRoom.setAddressLine2(room.getAddressLine2());
            updatedRoom.setCity(room.getCity());
            updatedRoom.setPostalCode(room.getPostalCode());

            List<String> imagePaths = updatedRoom.getImagePaths();
            if (images != null && !images.isEmpty()) {
                System.out.println("Processing " + images.size() + " images for update");
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                        Path filePath = uploadDir.resolve(fileName);
                        System.out.println("Saving image to: " + filePath);
                        try {
                            Files.write(filePath, image.getBytes());
                            System.out.println("Image saved successfully to: " + filePath);
                        } catch (IOException e) {
                            System.err.println("Failed to save image to " + filePath + ": " + e.getMessage());
                            throw e;
                        }
                        imagePaths.add(filePath.toString());
                    } else {
                        System.out.println("Skipping empty image file");
                    }
                }
                updatedRoom.setImagePaths(imagePaths);
            } else {
                System.out.println("No new images provided for update");
            }
            System.out.println("Image paths to save: " + imagePaths);

            Room savedRoom = roomRepository.save(updatedRoom);
            System.out.println("Room updated with ID: " + savedRoom.getRoomId());
            return savedRoom;
        }
        return null;
    }

    @Transactional
    public boolean deleteRoom(Long roomId) {
        try {
            Optional<Room> room = roomRepository.findById(roomId);
            if (room.isPresent()) {
                List<String> imagePaths = room.get().getImagePaths();
                for (String imagePath : imagePaths) {
                    Files.deleteIfExists(Paths.get(imagePath));
                }
                roomRepository.deleteById(roomId);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting room: " + e.getMessage());
            return false;
        }
    }

    public List<Room> getRoomsByOwnerId(Long ownerId) {
        return roomRepository.findByOwnerOwnerId(ownerId);
    }
}