package com.hotel.hotelbookingsystem.repository;

import com.hotel.hotelbookingsystem.entity.Booking;
import com.hotel.hotelbookingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.contactInformation WHERE b.user = :user ORDER BY b.createdAt DESC")
    List<Booking> findByUserOrderByCreatedAtDesc(@Param("user") User user);
    
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.contactInformation WHERE b.user = :user AND b.checkInDate >= :currentDate ORDER BY b.checkInDate ASC")
    List<Booking> findUpcomingBookingsByUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.contactInformation WHERE b.user = :user AND b.checkOutDate < :currentDate AND b.status != 'CANCELLED' ORDER BY b.checkOutDate DESC")
    List<Booking> findPastBookingsByUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
           "b.status IN ('PENDING', 'CONFIRMED') AND " +
           "((b.checkInDate <= :checkInDate AND b.checkOutDate > :checkInDate) OR " +
           "(b.checkInDate < :checkOutDate AND b.checkOutDate >= :checkOutDate) OR " +
           "(b.checkInDate >= :checkInDate AND b.checkOutDate <= :checkOutDate))")
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId, 
                                         @Param("checkInDate") LocalDate checkInDate, 
                                         @Param("checkOutDate") LocalDate checkOutDate);
}