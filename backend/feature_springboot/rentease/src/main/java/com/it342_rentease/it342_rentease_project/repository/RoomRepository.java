package com.it342_rentease.it342_rentease_project.repository;

import com.it342_rentease.it342_rentease_project.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    // Fetch rooms by ownerId
    List<Room> findByOwnerOwnerId(Long ownerId);
    List<Room> findByOwnerOwnerIdAndStatus(Long ownerId, String status);
    long countByOwnerOwnerId(Long ownerId);
long countByOwnerOwnerIdAndStatus(Long ownerId, String status);


}
