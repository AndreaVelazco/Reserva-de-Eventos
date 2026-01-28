package com.ebiz.eventosback.controller;

import com.ebiz.eventosback.dto.EventoDTO;
import com.ebiz.eventosback.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EventoController {
    
    private final EventoService eventoService;
    
    @GetMapping
    public ResponseEntity<List<EventoDTO>> obtenerTodosLosEventos() {
        List<EventoDTO> eventos = eventoService.obtenerTodosLosEventos();
        return ResponseEntity.ok(eventos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> obtenerEventoPorId(@PathVariable Long id) {
        EventoDTO evento = eventoService.obtenerEventoPorId(id);
        return ResponseEntity.ok(evento);
    }
    
    @GetMapping("/proximos")
    public ResponseEntity<List<EventoDTO>> obtenerEventosProximos() {
        List<EventoDTO> eventos = eventoService.obtenerEventosProximos();
        return ResponseEntity.ok(eventos);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoDTO> crearEvento(@Valid @RequestBody EventoDTO eventoDTO) {
        EventoDTO nuevoEvento = eventoService.crearEvento(eventoDTO);
        return new ResponseEntity<>(nuevoEvento, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoDTO> actualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody EventoDTO eventoDTO) {
        EventoDTO eventoActualizado = eventoService.actualizarEvento(id, eventoDTO);
        return ResponseEntity.ok(eventoActualizado);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }
}
