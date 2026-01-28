package com.ebiz.eventosback.controller;

import com.ebiz.eventosback.dto.ReservaDTO;
import com.ebiz.eventosback.dto.ReservaRequestDTO;
import com.ebiz.eventosback.service.ReservaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ReservaController {
    
    private final ReservaService reservaService;
    

    @PostMapping
    public ResponseEntity<ReservaDTO> crearReserva(
            @Valid @RequestBody ReservaRequestDTO requestDTO,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ReservaDTO reserva = reservaService.crearReserva(userId, requestDTO);
        return new ResponseEntity<>(reserva, HttpStatus.CREATED);
    }
    

    @PostMapping("/reservar")
    public ResponseEntity<ReservaDTO> crearReservaPendiente(
            @Valid @RequestBody ReservaRequestDTO requestDTO,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ReservaDTO reserva = reservaService.crearReservaPendiente(userId, requestDTO);
        return new ResponseEntity<>(reserva, HttpStatus.CREATED);
    }
    

    @PutMapping("/{id}/confirmar-pago")
    public ResponseEntity<ReservaDTO> confirmarPago(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ReservaDTO reserva = reservaService.confirmarPago(id, userId);
        return ResponseEntity.ok(reserva);
    }
    
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaDTO>> obtenerMisReservas(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ReservaDTO> reservas = reservaService.obtenerReservasPorUsuario(userId);
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtenerReservaPorId(@PathVariable Long id) {
        ReservaDTO reserva = reservaService.obtenerReservaPorId(id);
        return ResponseEntity.ok(reserva);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservaDTO>> obtenerTodasLasReservas() {
        List<ReservaDTO> reservas = reservaService.obtenerTodasLasReservas();
        return ResponseEntity.ok(reservas);
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarReserva(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        reservaService.cancelarReserva(id, userId);
        return ResponseEntity.ok().build();
    }
}