package com.ebiz.eventosback.controller;

import com.ebiz.eventosback.dto.AuthResponseDTO;
import com.ebiz.eventosback.dto.LoginRequestDTO;
import com.ebiz.eventosback.dto.RegisterRequestDTO;
import com.ebiz.eventosback.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") 
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO) { 
        AuthResponseDTO response = authService.register(requestDTO); 
        return new ResponseEntity<>(response, HttpStatus.CREATED); 
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        AuthResponseDTO response = authService.login(requestDTO);
        return ResponseEntity.ok(response);
    }
}
