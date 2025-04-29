package com.it342_rentease.it342_rentease_project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.it342_rentease.it342_rentease_project.model.Owner;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.OwnerRepository;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Autowired
    private RestTemplate restTemplate;

    private final String supabaseUrl;
    private final String supabaseKey;
    private final String bucketName;
    private final String storageUrl;

    public RoomService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.key}") String supabaseKey,
            @Value("${supabase.bucket}") String bucketName,
            @Value("${supabase.storage-url}") String storageUrl
    ) {
        this.supabaseUrl = supabaseUrl;
        this.supabaseKey = supabaseKey;
        this.bucketName = bucketName;
        this.storageUrl = storageUrl;
    }

    @Transactional
    public Room createRoom(Room room, List<MultipartFile> images) throws IOException {
        System.out.println("Creating room with data: " + room);
        if (room.getOwner() != null && room.getOwner().getOwnerId() != null) {
            Optional<Owner> owner = ownerRepository.findById(room.getOwner().getOwnerId());
            if (owner.isPresent()) {
                room.setOwner(owner.get());
                // Populate transient fields
                room.setOwnerId(owner.get().getOwnerId());
                room.setOwnerName(owner.get().getUsername());
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
                    System.out.println("Uploading image to Supabase: " + fileName);
                    try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setBearerAuth(supabaseKey);
                        headers.set("Content-Type", image.getContentType());

                        HttpEntity<byte[]> requestEntity = new HttpEntity<>(image.getBytes(), headers);

                        String uploadUrl = String.format("%s/%s/%s", storageUrl, bucketName, fileName);
                        ResponseEntity<String> response = restTemplate.exchange(
                                uploadUrl,
                                HttpMethod.POST,
                                requestEntity,
                                String.class
                        );

                        if (response.getStatusCode().is2xxSuccessful()) {
                            String publicUrl = String.format("%s/public/%s/%s", storageUrl, bucketName, fileName);
                            imagePaths.add(publicUrl);
                            System.out.println("Image uploaded successfully: " + publicUrl);
                        } else {
                            throw new IOException("Failed to upload image to Supabase: " + response.getStatusCode());
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to upload image to Supabase: " + e.getMessage());
                        throw new IOException("Failed to upload image to Supabase", e);
                    }
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

    @Transactional
    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        // Populate ownerId and ownerName for each room
        for (Room room : rooms) {
            if (room.getOwner() != null) {
                room.setOwnerId(room.getOwner().getOwnerId());
                room.setOwnerName(room.getOwner().getUsername());
            }
        }
        return rooms;
    }

    @Transactional
    public Optional<Room> getRoomById(Long roomId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        // Populate ownerId and ownerName if room exists
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            if (room.getOwner() != null) {
                room.setOwnerId(room.getOwner().getOwnerId());
                room.setOwnerName(room.getOwner().getUsername());
            }
        }
        return roomOptional;
    }

    @Transactional
    public Room updateRoom(Long roomId, Room room, List<MultipartFile> images, String removedImagesJson) throws IOException {
        System.out.println("Updating room with ID: " + roomId);
        Optional<Room> existingRoom = roomRepository.findById(roomId);
        if (existingRoom.isPresent()) {
            Room updatedRoom = existingRoom.get();
            if (room.getOwner() != null && room.getOwner().getOwnerId() != null) {
                Optional<Owner> owner = ownerRepository.findById(room.getOwner().getOwnerId());
                if (owner.isPresent()) {
                    updatedRoom.setOwner(owner.get());
                    updatedRoom.setOwnerId(owner.get().getOwnerId());
                    updatedRoom.setOwnerName(owner.get().getUsername());
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

            if (removedImagesJson != null && !removedImagesJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> removedImages;
                try {
                    removedImages = objectMapper.readValue(removedImagesJson, new TypeReference<List<String>>(){});
                } catch (Exception e) {
                    throw new IOException("Failed to parse removedImages JSON", e);
                }

                for (String imagePath : removedImages) {
                    if (imagePath != null && !imagePath.isEmpty()) {
                        imagePaths.remove(imagePath);
                        String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                        System.out.println("Deleting image from Supabase: " + fileName);
                        try {
                            HttpHeaders headers = new HttpHeaders();
                            headers.setBearerAuth(supabaseKey);
                            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
                            String deleteUrl = String.format("%s/%s/%s", storageUrl, bucketName, fileName);
                            ResponseEntity<String> response = restTemplate.exchange(
                                    deleteUrl,
                                    HttpMethod.DELETE,
                                    requestEntity,
                                    String.class
                            );

                            if (response.getStatusCode().is2xxSuccessful()) {
                                System.out.println("Image deleted successfully: " + fileName);
                            } else {
                                System.err.println("Failed to delete image from Supabase: " + response.getStatusCode());
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to delete image from Supabase: " + e.getMessage());
                        }
                    }
                }
            }

            if (images != null && !images.isEmpty()) {
                System.out.println("Processing " + images.size() + " images for update");
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                        System.out.println("Uploading image to Supabase: " + fileName);
                        try {
                            HttpHeaders headers = new HttpHeaders();
                            headers.setBearerAuth(supabaseKey);
                            headers.set("Content-Type", image.getContentType());

                            HttpEntity<byte[]> requestEntity = new HttpEntity<>(image.getBytes(), headers);

                            String uploadUrl = String.format("%s/%s/%s", storageUrl, bucketName, fileName);
                            ResponseEntity<String> response = restTemplate.exchange(
                                    uploadUrl,
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class
                            );

                            if (response.getStatusCode().is2xxSuccessful()) {
                                String publicUrl = String.format("%s/public/%s/%s", storageUrl, bucketName, fileName);
                                imagePaths.add(publicUrl);
                                System.out.println("Image uploaded successfully: " + publicUrl);
                            } else {
                                throw new IOException("Failed to upload image to Supabase: " + response.getStatusCode());
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to upload image to Supabase: " + e.getMessage());
                            throw new IOException("Failed to upload image to Supabase", e);
                        }
                    } else {
                        System.out.println("Skipping empty image file");
                    }
                }
            } else {
                System.out.println("No new images provided for update");
            }

            updatedRoom.setImagePaths(imagePaths);
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
                    String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                    System.out.println("Deleting image from Supabase: " + fileName);
                    try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setBearerAuth(supabaseKey);
                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
                        String deleteUrl = String.format("%s/%s/%s", storageUrl, bucketName, fileName);
                        ResponseEntity<String> response = restTemplate.exchange(
                                deleteUrl,
                                HttpMethod.DELETE,
                                requestEntity,
                                String.class
                        );

                        if (response.getStatusCode().is2xxSuccessful()) {
                            System.out.println("Image deleted successfully: " + fileName);
                        } else {
                            System.err.println("Failed to delete image from Supabase: " + response.getStatusCode());
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to delete image from Supabase: " + e.getMessage());
                    }
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

    @Transactional
    public List<Room> getRoomsByOwnerId(Long ownerId) {
        List<Room> rooms = roomRepository.findByOwnerOwnerId(ownerId);
        // Populate ownerId and ownerName for each room
        for (Room room : rooms) {
            if (room.getOwner() != null) {
                room.setOwnerId(room.getOwner().getOwnerId());
                room.setOwnerName(room.getOwner().getUsername());
            }
        }
        return rooms;
    }

    @Transactional
    public List<Room> getUnavailableRoomsByOwnerId(Long ownerId) {
        List<Room> rooms = roomRepository.findByOwnerOwnerIdAndStatus(ownerId, "rented");
        // Populate ownerId and ownerName for each room
        for (Room room : rooms) {
            if (room.getOwner() != null) {
                room.setOwnerId(room.getOwner().getOwnerId());
                room.setOwnerName(room.getOwner().getUsername());
            }
        }
        return rooms;
    }
}