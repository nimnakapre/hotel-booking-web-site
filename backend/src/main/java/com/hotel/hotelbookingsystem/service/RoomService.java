package com.hotel.hotelbookingsystem.service;

import com.hotel.hotelbookingsystem.entity.Room;
import com.hotel.hotelbookingsystem.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
    
    public List<Room> getAvailableRooms() {
        return roomRepository.findByIsAvailableTrue();
    }
    
    public List<Room> getAvailableRoomsForDateRange(LocalDate checkInDate, LocalDate checkOutDate) {
        return roomRepository.findAvailableRoomsForDateRange(checkInDate, checkOutDate);
    }
    
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }
    
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }
}