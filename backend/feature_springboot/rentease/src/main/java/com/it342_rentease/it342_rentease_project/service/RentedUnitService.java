package com.it342_rentease.it342_rentease_project.service;

import com.it342_rentease.it342_rentease_project.model.PaymentReminder;
import com.it342_rentease.it342_rentease_project.model.RentedUnit;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.PaymentReminderRepository;
import com.it342_rentease.it342_rentease_project.repository.RentedUnitRepository;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RentedUnitService {

    @Autowired
    private RentedUnitRepository rentedUnitRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PaymentReminderRepository paymentReminderRepository;

    @Transactional
    public RentedUnit save(RentedUnit rentedUnit) {
        Long roomId = rentedUnit.getRoom().getRoomId();
        Optional<Room> roomOptional = roomRepository.findById(roomId);

        if (roomOptional.isEmpty()) {
            throw new IllegalArgumentException("Room not found with ID: " + roomId);
        }

        Room room = roomOptional.get();

        if ("rented".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalStateException("This room is already rented.");
        }

        // ✅ Mark room as rented TEMPORARILY (still needs owner approval)
        room.setStatus("unavailable");
        roomRepository.save(room);

        // Save rented unit
        rentedUnit.setRoom(room);
        RentedUnit savedUnit = rentedUnitRepository.save(rentedUnit);

        // 🔍 Check if reminder already exists
        boolean pendingReminderExists = paymentReminderRepository
                .findByRenterRenterId(rentedUnit.getRenter().getRenterId())
                .stream()
                .anyMatch(r -> r.getRoom().getRoomId().equals(roomId)
                        && "pending".equalsIgnoreCase(r.getApprovalStatus()));

        if (!pendingReminderExists) {
            // ✅ No pending reminder — OK to create a new reminder
            PaymentReminder reminder = new PaymentReminder();
            reminder.setRoom(room);
            reminder.setRenter(rentedUnit.getRenter());
            reminder.setOwner(room.getOwner());
            reminder.setDueDate(rentedUnit.getStartDate());
            reminder.setRentalFee(room.getRentalFee());
            reminder.setNote("Booking pending approval for " + rentedUnit.getStartDate());
            reminder.setApprovalStatus("pending");

            paymentReminderRepository.save(reminder);
        }

        return savedUnit;
    }

    public List<RentedUnit> getAll() {
        return rentedUnitRepository.findAll();
    }

    public Optional<RentedUnit> getById(Long id) {
        return rentedUnitRepository.findById(id);
    }

    public List<RentedUnit> getByRenterId(Long renterId) {
        return rentedUnitRepository.findByRenterRenterId(renterId);
    }

    public List<RentedUnit> getByRoomId(Long roomId) {
        return rentedUnitRepository.findByRoomRoomId(roomId);
    }

    @Transactional
    public void delete(Long id) {
        Optional<RentedUnit> rentedUnitOptional = rentedUnitRepository.findById(id);

        if (rentedUnitOptional.isPresent()) {
            RentedUnit rentedUnit = rentedUnitOptional.get();
            Room room = rentedUnit.getRoom();

            // 🛠 Step 1: Delete rented unit
            rentedUnitRepository.deleteById(id);

            // 🛠 Step 2: Set room status back to "available"
            room.setStatus("available");
            roomRepository.save(room);
        } else {
            throw new IllegalArgumentException("Rented unit not found with ID: " + id);
        }
    }

}
