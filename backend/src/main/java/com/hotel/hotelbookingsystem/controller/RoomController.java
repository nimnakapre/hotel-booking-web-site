package com.hotel.hotelbookingsystem.controller;

import com.hotel.hotelbookingsystem.entity.Room;
import com.hotel.hotelbookingsystem.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    
    @Autowired
    private RoomService roomService;
    
    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }
    
    @GetMapping("/available")
    public List<Room> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }
    
    @GetMapping("/available/search")
    public List<Room> getAvailableRoomsForDateRange(
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate) {
        return roomService.getAvailableRoomsForDateRange(checkInDate, checkOutDate);
    }
    
    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }
}