package com.hotel.hotelbookingsystem.controller;

import com.hotel.hotelbookingsystem.dto.BookingRequest;
import com.hotel.hotelbookingsystem.entity.Booking;
import com.hotel.hotelbookingsystem.entity.User;
import com.hotel.hotelbookingsystem.security.UserPrincipal;
import com.hotel.hotelbookingsystem.service.BookingService;
import com.hotel.hotelbookingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest bookingRequest, 
                                         Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByUsername(userPrincipal.getUsername());
            
            Booking booking = bookingService.createBooking(user, bookingRequest);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping
    public List<Booking> getUserBookings(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.findByUsername(userPrincipal.getUsername());
        
        return bookingService.getUserBookings(user);
    }
    
    @GetMapping("/upcoming")
    public List<Booking> getUpcomingBookings(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.findByUsername(userPrincipal.getUsername());
        
        return bookingService.getUpcomingBookings(user);
    }
    
    @GetMapping("/past")
    public List<Booking> getPastBookings(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.findByUsername(userPrincipal.getUsername());
        
        return bookingService.getPastBookings(user);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByUsername(userPrincipal.getUsername());
            
            Booking booking = bookingService.getBookingById(id);
            
            // Check if the booking belongs to the authenticated user
            if (!booking.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("You can only view your own bookings");
            }
            
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userService.findByUsername(userPrincipal.getUsername());
            
            Booking booking = bookingService.cancelBooking(id, user);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}