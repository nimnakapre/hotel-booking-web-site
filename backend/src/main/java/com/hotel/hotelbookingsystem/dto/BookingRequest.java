package com.hotel.hotelbookingsystem.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class BookingRequest {
    
    @NotNull
    private Long roomId;
    
    @NotNull
    @Future
    private LocalDate checkInDate;
    
    @NotNull
    @Future
    private LocalDate checkOutDate;
    
    @NotNull
    @Positive
    private Integer numberOfGuests;
    
    @Valid
    private ContactInformationRequest contactInformation;
    
    // Constructors
    public BookingRequest() {}
    
    public BookingRequest(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests) {
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
    }
    
    public BookingRequest(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests, ContactInformationRequest contactInformation) {
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.contactInformation = contactInformation;
    }
    
    // Getters and Setters
    public Long getRoomId() {
        return roomId;
    }
    
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    
    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }
    
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
    
    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    
    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }
    
    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
    
    public ContactInformationRequest getContactInformation() {
        return contactInformation;
    }
    
    public void setContactInformation(ContactInformationRequest contactInformation) {
        this.contactInformation = contactInformation;
    }
}