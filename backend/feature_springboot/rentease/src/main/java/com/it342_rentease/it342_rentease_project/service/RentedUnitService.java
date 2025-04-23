package com.it342_rentease.it342_rentease_project.service;

import com.it342_rentease.it342_rentease_project.model.PaymentReminder;
import com.it342_rentease.it342_rentease_project.model.RentedUnit;
import com.it342_rentease.it342_rentease_project.model.Room;
import com.it342_rentease.it342_rentease_project.repository.PaymentReminderRepository;
import com.it342_rentease.it342_rentease_project.repository.RentedUnitRepository;
import com.it342_rentease.it342_rentease_project.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
        // Mark as rented
        room.setStatus("rented");
        roomRepository.save(room);
    
        // Save rented unit
        rentedUnit.setRoom(room); // ensure full room object
        RentedUnit savedUnit = rentedUnitRepository.save(rentedUnit);
    
        // Create payment reminder
        PaymentReminder reminder = new PaymentReminder();
        reminder.setRoom(room);
        reminder.setRenter(rentedUnit.getRenter());
        reminder.setOwner(room.getOwner()); // ✅ Now this won't be null
        reminder.setDueDate(rentedUnit.getStartDate());
        reminder.setRentalFee(room.getRentalFee());
        reminder.setNote("Payment is due on " + rentedUnit.getStartDate());
    
        paymentReminderRepository.save(reminder);
    
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

    public void delete(Long id) {
        rentedUnitRepository.deleteById(id);
    }


}
