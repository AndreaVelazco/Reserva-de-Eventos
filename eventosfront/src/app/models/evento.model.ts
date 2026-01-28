export interface Evento {
  id?: number;
  nombre: string;
  descripcion: string;
  fecha: string;
  ubicacion: string;
  capacidadTotal: number;
  entradasDisponibles?: number;
  precioEntrada: number;
}