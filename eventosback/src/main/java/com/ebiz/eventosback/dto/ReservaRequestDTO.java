package com.ebiz.eventosback.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {
    
    @NotNull(message = "El evento es obligatorio")
    private Long eventoId;
    
    @NotNull(message = "La cantidad de entradas es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private Integer cantidadEntradas;
}