package com.hotel.hotelbookingsystem.service;

import com.hotel.hotelbookingsystem.dto.ContactInformationRequest;
import com.hotel.hotelbookingsystem.entity.ContactInformation;
import com.hotel.hotelbookingsystem.repository.ContactInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContactInformationService {
    
    @Autowired
    private ContactInformationRepository contactInformationRepository;
    
    public ContactInformation createContactInformation(ContactInformationRequest request) {
        ContactInformation contactInformation = new ContactInformation(
            request.getTitle(),
            request.getFullName(),
            request.getEmail(),
            request.getPhone()
        );
        
        return contactInformationRepository.save(contactInformation);
    }
    
    public ContactInformation getContactInformationById(Long id) {
        return contactInformationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact information not found with id: " + id));
    }
    
    public Optional<ContactInformation> getContactInformationByEmail(String email) {
        return contactInformationRepository.findByEmail(email);
    }
    
    public ContactInformation updateContactInformation(Long id, ContactInformationRequest request) {
        ContactInformation contactInformation = getContactInformationById(id);
        
        contactInformation.setTitle(request.getTitle());
        contactInformation.setFullName(request.getFullName());
        contactInformation.setEmail(request.getEmail());
        contactInformation.setPhone(request.getPhone());
        
        return contactInformationRepository.save(contactInformation);
    }
    
    public void deleteContactInformation(Long id) {
        ContactInformation contactInformation = getContactInformationById(id);
        contactInformationRepository.delete(contactInformation);
    }
    
    public ContactInformation findOrCreateContactInformation(ContactInformationRequest request) {
        // Try to find existing contact by email
        Optional<ContactInformation> existingContact = getContactInformationByEmail(request.getEmail());
        
        if (existingContact.isPresent()) {
            // Update existing contact if any information has changed
            ContactInformation contact = existingContact.get();
            boolean needsUpdate = !contact.getTitle().equals(request.getTitle()) ||
                                !contact.getFullName().equals(request.getFullName()) ||
                                !contact.getEmail().equals(request.getEmail()) ||
                                (contact.getPhone() == null ? request.getPhone() != null : 
                                 !contact.getPhone().equals(request.getPhone()));
            
            if (needsUpdate) {
                contact.setTitle(request.getTitle());
                contact.setFullName(request.getFullName());
                contact.setEmail(request.getEmail());
                contact.setPhone(request.getPhone());
                return contactInformationRepository.save(contact);
            }
            
            return contact;
        } else {
            // Create new contact
            return createContactInformation(request);
        }
    }
}