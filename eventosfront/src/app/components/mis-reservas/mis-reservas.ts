import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReservaService } from '../../services/reserva';
import { AuthService } from '../../services/auth';
import { Reserva } from '../../models/reserva.model';

@Component({
  selector: 'app-mis-reservas',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './mis-reservas.html',
  styleUrl: './mis-reservas.css'
})
export class MisReservasComponent implements OnInit {
  reservas: Reserva[] = [];
  loading: boolean = true;

  constructor(
    private reservaService: ReservaService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarReservas();
  }

cargarReservas(): void {
  const request = this.authService.isAdmin() 
    ? this.reservaService.obtenerTodas()
    : this.reservaService.obtenerMisReservas();
    
  request.subscribe({
    next: (data) => {
      this.reservas = data;
      this.loading = false;
    },
    error: (error) => {
      console.error('Error al cargar reservas:', error);
      this.loading = false;
    }
  });
}

  cancelarReserva(id: number): void {
    if (confirm('¿Está seguro de cancelar esta reserva?')) {
      this.reservaService.cancelar(id).subscribe({
        next: () => {
          alert('Reserva cancelada exitosamente');
          this.cargarReservas();
        },
        error: (error) => {
          console.error('Error al cancelar reserva:', error);
          alert('Error al cancelar la reserva');
        }
      });
    }
  }
  pagarReserva(id: number): void {
  if (confirm('¿Deseas proceder con el pago de esta reserva?')) {
    this.reservaService.confirmarPago(id).subscribe({
      next: () => {
        alert('¡Pago confirmado exitosamente!');
        this.cargarReservas();
      },
      error: (error) => {
        console.error('Error al confirmar pago:', error);
        alert('Error al confirmar el pago');
      }
    });
  }
}

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-PE', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getEstadoBadgeClass(estado: string): string {
    switch (estado) {
      case 'CONFIRMADA': return 'bg-success';
      case 'PENDIENTE': return 'bg-warning';
      case 'CANCELADA': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }
}