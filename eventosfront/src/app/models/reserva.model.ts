export interface Reserva {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  eventoId: number;
  eventoNombre: string;
  eventoFecha: string;
  cantidadEntradas: number;
  fechaReserva: string;
  estado: string;
  montoTotal: number;
}

export interface ReservaRequest {
  eventoId: number;
  cantidadEntradas: number;
}