package com.ebiz.eventosback.repository;

import com.ebiz.eventosback.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    List<Evento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin); 
    
    List<Evento> findByFechaAfter(LocalDateTime fecha);
    
    List<Evento> findAllByOrderByFechaAsc();
}
