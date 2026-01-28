package com.ebiz.eventosback.service;

import com.ebiz.eventosback.dto.ReservaDTO;
import com.ebiz.eventosback.dto.ReservaRequestDTO;
import com.ebiz.eventosback.exception.BadRequestException;
import com.ebiz.eventosback.exception.ResourceNotFoundException;
import com.ebiz.eventosback.model.Evento;
import com.ebiz.eventosback.model.Reserva;
import com.ebiz.eventosback.model.Usuario;
import com.ebiz.eventosback.repository.EventoRepository;
import com.ebiz.eventosback.repository.ReservaRepository;
import com.ebiz.eventosback.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoService eventoService;
    
    @Transactional
    public ReservaDTO crearReserva(Long usuarioId, ReservaRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId)); 
        
        if (usuario.getRol() == Usuario.Rol.ADMIN) {
            throw new BadRequestException("Los administradores no pueden realizar compras");
        }
        
        Evento evento = eventoRepository.findById(requestDTO.getEventoId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento", "id", requestDTO.getEventoId()));
        

        if (evento.getFecha().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se pueden hacer reservas para eventos pasados");
        }
        
        if (evento.getEntradasDisponibles() < requestDTO.getCantidadEntradas()) {
            throw new BadRequestException("No hay suficientes entradas disponibles. Solo quedan: " + evento.getEntradasDisponibles());
        }
        

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setEvento(evento);
        reserva.setCantidadEntradas(requestDTO.getCantidadEntradas());
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
        reserva.setMontoTotal(evento.getPrecioEntrada() * requestDTO.getCantidadEntradas());
        

        eventoService.actualizarEntradasDisponibles(evento.getId(), requestDTO.getCantidadEntradas());
        
        Reserva reservaGuardada = reservaRepository.save(reserva);
        return convertirADTO(reservaGuardada);
    }
    

    @Transactional
    public ReservaDTO crearReservaPendiente(Long usuarioId, ReservaRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        

            if (usuario.getRol() == Usuario.Rol.ADMIN) {
            throw new BadRequestException("Los administradores no pueden realizar reservas");
        }

        
        Evento evento = eventoRepository.findById(requestDTO.getEventoId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento", "id", requestDTO.getEventoId()));
        
        
        if (evento.getFecha().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se pueden hacer reservas para eventos pasados");
        }
        
        if (evento.getEntradasDisponibles() < requestDTO.getCantidadEntradas()) {
            throw new BadRequestException("No hay suficientes entradas disponibles. Solo quedan: " + evento.getEntradasDisponibles());
        }
        
        
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setEvento(evento);
        reserva.setCantidadEntradas(requestDTO.getCantidadEntradas());
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reserva.setMontoTotal(evento.getPrecioEntrada() * requestDTO.getCantidadEntradas());
        
        eventoService.actualizarEntradasDisponibles(evento.getId(), requestDTO.getCantidadEntradas());
        
        Reserva reservaGuardada = reservaRepository.save(reserva);
        return convertirADTO(reservaGuardada);
    }
    
    
    @Transactional
    public ReservaDTO confirmarPago(Long reservaId, Long usuarioId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", reservaId));
        
        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para confirmar esta reserva");
        }
        
        if (reserva.getEstado() != Reserva.EstadoReserva.PENDIENTE) {
            throw new BadRequestException("Esta reserva no está pendiente de pago");
        }
        
        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
        Reserva reservaActualizada = reservaRepository.save(reserva);
        return convertirADTO(reservaActualizada);
    }
    
    public List<ReservaDTO> obtenerReservasPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioIdOrderByFechaReservaDesc(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public ReservaDTO obtenerReservaPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        return convertirADTO(reserva);
    }
    
    public List<ReservaDTO> obtenerTodasLasReservas() {
        return reservaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelarReserva(Long id, Long usuarioId) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para cancelar esta reserva");
        }
        
        if (reserva.getEstado() == Reserva.EstadoReserva.CANCELADA) {
            throw new BadRequestException("La reserva ya está cancelada");
        }
        
        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
        
        
        Evento evento = reserva.getEvento();
        evento.setEntradasDisponibles(evento.getEntradasDisponibles() + reserva.getCantidadEntradas());
        eventoRepository.save(evento);
    }
    
    private ReservaDTO convertirADTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(reserva.getId());
        dto.setUsuarioId(reserva.getUsuario().getId());
        dto.setUsuarioNombre(reserva.getUsuario().getNombre());
        dto.setEventoId(reserva.getEvento().getId());
        dto.setEventoNombre(reserva.getEvento().getNombre());
        dto.setEventoFecha(reserva.getEvento().getFecha());
        dto.setCantidadEntradas(reserva.getCantidadEntradas());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setEstado(reserva.getEstado().name());
        dto.setMontoTotal(reserva.getMontoTotal());
        return dto;
    }
}