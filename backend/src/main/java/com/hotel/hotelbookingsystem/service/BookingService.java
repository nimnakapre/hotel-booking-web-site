package com.hotel.hotelbookingsystem.service;

import com.hotel.hotelbookingsystem.dto.BookingRequest;
import com.hotel.hotelbookingsystem.entity.Booking;
import com.hotel.hotelbookingsystem.entity.ContactInformation;
import com.hotel.hotelbookingsystem.entity.Room;
import com.hotel.hotelbookingsystem.entity.User;
import com.hotel.hotelbookingsystem.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private ContactInformationService contactInformationService;
    
    public Booking createBooking(User user, BookingRequest bookingRequest) {
        Room room = roomService.getRoomById(bookingRequest.getRoomId());
        
        // Check if room is available for the requested dates
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
            bookingRequest.getRoomId(),
            bookingRequest.getCheckInDate(),
            bookingRequest.getCheckOutDate()
        );
        
        if (!conflictingBookings.isEmpty()) {
            throw new RuntimeException("Room is not available for the selected dates");
        }
        
        // Calculate total amount
        long nights = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        
        // Handle contact information if provided
        ContactInformation contactInformation = null;
        if (bookingRequest.getContactInformation() != null) {
            contactInformation = contactInformationService.findOrCreateContactInformation(bookingRequest.getContactInformation());
        }
        
        Booking booking;
        if (contactInformation != null) {
            booking = new Booking(
                user,
                room,
                contactInformation,
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getNumberOfGuests(),
                totalAmount
            );
        } else {
            booking = new Booking(
                user,
                room,
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getNumberOfGuests(),
                totalAmount
            );
        }
        
        return bookingRepository.save(booking);
    }
    
    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public List<Booking> getUpcomingBookings(User user) {
        return bookingRepository.findUpcomingBookingsByUser(user, LocalDate.now());
    }
    
    public List<Booking> getPastBookings(User user) {
        return bookingRepository.findPastBookingsByUser(user, LocalDate.now());
    }
    
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }
    
    public Booking cancelBooking(Long bookingId, User user) {
        Booking booking = getBookingById(bookingId);
        
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only cancel your own bookings");
        }
        
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }
        
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed booking");
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
}