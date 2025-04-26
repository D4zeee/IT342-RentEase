package com.it342_rentease.it342_rentease_project.controller;

import com.it342_rentease.it342_rentease_project.model.RentedUnit;
import com.it342_rentease.it342_rentease_project.service.RentedUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rented_units")
@CrossOrigin(origins = "http://localhost:5173")
public class RentedUnitController {

    @Autowired
    private RentedUnitService rentedUnitService;

    @PostMapping
    public ResponseEntity<RentedUnit> create(@RequestBody RentedUnit unit) {
        return new ResponseEntity<>(rentedUnitService.save(unit), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RentedUnit>> getAll() {
        return new ResponseEntity<>(rentedUnitService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentedUnit> getById(@PathVariable Long id) {
        Optional<RentedUnit> unit = rentedUnitService.getById(id);
        return unit.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/renter/{renterId}")
    public ResponseEntity<List<RentedUnit>> getByRenter(@PathVariable Long renterId) {
        return new ResponseEntity<>(rentedUnitService.getByRenterId(renterId), HttpStatus.OK);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<RentedUnit>> getByRoom(@PathVariable Long roomId) {
        return new ResponseEntity<>(rentedUnitService.getByRoomId(roomId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentedUnitService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}