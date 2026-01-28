import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reserva, ReservaRequest } from '../models/reserva.model';

@Injectable({
  providedIn: 'root'
})
export class ReservaService {
  private apiUrl = 'http://localhost:8080/ms-bootcamp/reservas';

  constructor(private http: HttpClient) { }

  crear(request: ReservaRequest): Observable<Reserva> {
    return this.http.post<Reserva>(this.apiUrl, request);
  }

  obtenerMisReservas(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.apiUrl}/mis-reservas`);
  }

  obtenerPorId(id: number): Observable<Reserva> {
    return this.http.get<Reserva>(`${this.apiUrl}/${id}`);
  }

  obtenerTodas(): Observable<Reserva[]> {
  return this.http.get<Reserva[]>(this.apiUrl);
}

  cancelar(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/cancelar`, {});
  }
  crearReservaPendiente(request: ReservaRequest): Observable<Reserva> {
  return this.http.post<Reserva>(`${this.apiUrl}/reservar`, request);
}

confirmarPago(id: number): Observable<Reserva> {
  return this.http.put<Reserva>(`${this.apiUrl}/${id}/confirmar-pago`, {});
}
}
