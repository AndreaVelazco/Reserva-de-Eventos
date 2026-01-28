package com.ebiz.eventosback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eventos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(length = 1000) 
    private String descripcion; 
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(nullable = false)
    private String ubicacion;
    
    @Column(nullable = false)
    private Integer capacidadTotal;
    
    @Column(nullable = false)
    private Integer entradasDisponibles;
    
    @Column(nullable = false)
    private Double precioEntrada;
    
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<>(); 
}
