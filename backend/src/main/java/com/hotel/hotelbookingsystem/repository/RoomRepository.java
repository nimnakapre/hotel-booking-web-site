package com.hotel.hotelbookingsystem.repository;

import com.hotel.hotelbookingsystem.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByIsAvailableTrue();
    
    List<Room> findByRoomTypeAndIsAvailableTrue(String roomType);
    
    @Query("SELECT r FROM Room r WHERE r.id NOT IN " +
           "(SELECT b.room.id FROM Booking b WHERE " +
           "b.status IN ('PENDING', 'CONFIRMED') AND " +
           "((b.checkInDate <= :checkInDate AND b.checkOutDate > :checkInDate) OR " +
           "(b.checkInDate < :checkOutDate AND b.checkOutDate >= :checkOutDate) OR " +
           "(b.checkInDate >= :checkInDate AND b.checkOutDate <= :checkOutDate)))")
    List<Room> findAvailableRoomsForDateRange(@Param("checkInDate") LocalDate checkInDate, 
                                             @Param("checkOutDate") LocalDate checkOutDate);
}