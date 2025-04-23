package com.it342_rentease.it342_rentease_project.repository;

import com.it342_rentease.it342_rentease_project.model.RentedUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentedUnitRepository extends JpaRepository<RentedUnit, Long> {
    List<RentedUnit> findByRenterRenterId(Long renterId);
    List<RentedUnit> findByRoomRoomId(Long roomId);
    boolean existsByRoom_RoomIdAndRenter_RenterId(Long roomId, Long renterId);
}
