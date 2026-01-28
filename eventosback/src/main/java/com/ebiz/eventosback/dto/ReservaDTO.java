package com.ebiz.eventosback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre; 
    private Long eventoId;
    private String eventoNombre;
    private LocalDateTime eventoFecha;
    private Integer cantidadEntradas;
    private LocalDateTime fechaReserva;
    private String estado;
    private Double montoTotal;
}
