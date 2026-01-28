package com.ebiz.eventosback.service;

import com.ebiz.eventosback.dto.EventoDTO;
import com.ebiz.eventosback.exception.BadRequestException;
import com.ebiz.eventosback.exception.ResourceNotFoundException;
import com.ebiz.eventosback.model.Evento;
import com.ebiz.eventosback.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {
    
    private final EventoRepository eventoRepository;
    
    @Transactional
    public EventoDTO crearEvento(EventoDTO eventoDTO) {
        if (eventoDTO.getFecha().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha del evento no puede ser en el pasado");
        }

        //crear evento
        Evento evento = new Evento();
        evento.setNombre(eventoDTO.getNombre());
        evento.setDescripcion(eventoDTO.getDescripcion());
        evento.setFecha(eventoDTO.getFecha());
        evento.setUbicacion(eventoDTO.getUbicacion());
        evento.setCapacidadTotal(eventoDTO.getCapacidadTotal());
        evento.setEntradasDisponibles(eventoDTO.getCapacidadTotal());
        evento.setPrecioEntrada(eventoDTO.getPrecioEntrada());
        
        Evento eventoGuardado = eventoRepository.save(evento);
        return convertirADTO(eventoGuardado); // devolver el evento creado como DTO
    }
    
    public List<EventoDTO> obtenerTodosLosEventos() {
        return eventoRepository.findAllByOrderByFechaAsc().stream() 
                .map(this::convertirADTO) 
                .collect(Collectors.toList()); 
    }
    
    public EventoDTO obtenerEventoPorId(Long id) {
        Evento evento = eventoRepository.findById(id) 
                .orElseThrow(() -> new ResourceNotFoundException("Evento", "id", id)); 
        return convertirADTO(evento);
    }
    
    public List<EventoDTO> obtenerEventosProximos() { 
        LocalDateTime ahora = LocalDateTime.now(); 
        return eventoRepository.findByFechaAfter(ahora).stream() 
                .map(this::convertirADTO) 
                .collect(Collectors.toList()); 
    }
    
    @Transactional
    public EventoDTO actualizarEvento(Long id, EventoDTO eventoDTO) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", "id", id));
        
        evento.setNombre(eventoDTO.getNombre());
        evento.setDescripcion(eventoDTO.getDescripcion());
        evento.setFecha(eventoDTO.getFecha());
        evento.setUbicacion(eventoDTO.getUbicacion());
        evento.setCapacidadTotal(eventoDTO.getCapacidadTotal());
        evento.setPrecioEntrada(eventoDTO.getPrecioEntrada());
        
        Evento eventoActualizado = eventoRepository.save(evento);
        return convertirADTO(eventoActualizado);
    }
    
    @Transactional
    public void eliminarEvento(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", "id", id));
        eventoRepository.delete(evento);
    }
    
    @Transactional
    public void actualizarEntradasDisponibles(Long eventoId, int cantidad) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", "id", eventoId));
        
        int nuevasEntradasDisponibles = evento.getEntradasDisponibles() - cantidad;
        
        if (nuevasEntradasDisponibles < 0) {
            throw new BadRequestException("No hay suficientes entradas disponibles");
        }
        
        evento.setEntradasDisponibles(nuevasEntradasDisponibles);
        eventoRepository.save(evento);
    }
    
    private EventoDTO convertirADTO(Evento evento) {
        EventoDTO dto = new EventoDTO();
        dto.setId(evento.getId());
        dto.setNombre(evento.getNombre());
        dto.setDescripcion(evento.getDescripcion());
        dto.setFecha(evento.getFecha());
        dto.setUbicacion(evento.getUbicacion());
        dto.setCapacidadTotal(evento.getCapacidadTotal());
        dto.setEntradasDisponibles(evento.getEntradasDisponibles());
        dto.setPrecioEntrada(evento.getPrecioEntrada());
        return dto;
    }
}
