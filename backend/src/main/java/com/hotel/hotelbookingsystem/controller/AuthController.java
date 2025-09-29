package com.hotel.hotelbookingsystem.controller;

import com.hotel.hotelbookingsystem.dto.JwtResponse;
import com.hotel.hotelbookingsystem.dto.LoginRequest;
import com.hotel.hotelbookingsystem.dto.RegisterRequest;
import com.hotel.hotelbookingsystem.entity.User;
import com.hotel.hotelbookingsystem.security.UserPrincipal;
import com.hotel.hotelbookingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.hotel.hotelbookingsystem.security.JwtUtils;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserService userService;
    
    @Autowired
    JwtUtils jwtUtils;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        // Load full user to return names
        User user = userService.findByUsername(userPrincipal.getUsername());

        JwtResponse resp = new JwtResponse();
        resp.setToken(jwt);
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setFirstName(user.getFirstName());
        resp.setLastName(user.getLastName());
        resp.setRoles(userPrincipal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList());

        return ResponseEntity.ok(resp);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        try {
            userService.createUser(signUpRequest);
            return ResponseEntity.ok("User registered successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsernameAvailability(@RequestParam String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = userService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }
}