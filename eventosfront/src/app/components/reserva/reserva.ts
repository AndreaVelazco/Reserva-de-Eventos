import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { EventoService } from '../../services/evento';
import { ReservaService } from '../../services/reserva';
import { Evento } from '../../models/evento.model';
import { ReservaRequest } from '../../models/reserva.model';

@Component({
  selector: 'app-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './reserva.html',
  styleUrl: './reserva.css'
})
export class ReservaComponent implements OnInit {
  evento: Evento | null = null;
  cantidadEntradas: number = 1;
  metodoPago: string = 'tarjeta';
  loading: boolean = true;
  procesando: boolean = false;
  errorMessage: string = '';
  pasoActual: number = 1;
  tipoOperacion: 'compra' | 'reserva' = 'compra';

  datosPago = {
    nombreTitular: '',
    numeroTarjeta: '',
    fechaVencimiento: '',
    cvv: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventoService: EventoService,
    private reservaService: ReservaService
  ) {}

  ngOnInit(): void {
    const eventoId = Number(this.route.snapshot.paramMap.get('eventoId'));
    this.tipoOperacion = this.route.snapshot.queryParamMap.get('tipo') as 'compra' | 'reserva' || 'compra';
    
    if (eventoId) {
      this.cargarEvento(eventoId);
    }
  }

  cargarEvento(id: number): void {
    this.eventoService.obtenerPorId(id).subscribe({
      next: (data) => {
        this.evento = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar evento:', error);
        this.router.navigate(['/eventos']);
      }
    });
  }

  calcularSubtotal(): number {
    return this.cantidadEntradas * (this.evento?.precioEntrada || 0);
  }

  calcularComision(): number {
    return this.calcularSubtotal() * 0.05;
  }

  calcularTotal(): number {
    return this.calcularSubtotal() + this.calcularComision();
  }

  siguientePaso(): void {
    if (this.pasoActual === 1) {
      if (this.cantidadEntradas < 1 || this.cantidadEntradas > (this.evento?.entradasDisponibles || 0)) {
        this.errorMessage = 'Cantidad de entradas inválida';
        return;
      }
      
      // Si es RESERVA, saltar el paso de pago
      if (this.tipoOperacion === 'reserva') {
        this.procesarCompra();
      } else {
        this.pasoActual = 2;
      }
      this.errorMessage = '';
    } else if (this.pasoActual === 2) {
      if (this.metodoPago === 'tarjeta') {
        if (!this.validarDatosTarjeta()) {
          this.errorMessage = 'Por favor completa todos los datos de la tarjeta';
          return;
        }
      }
      this.procesarCompra();
    }
  }

  pasoAnterior(): void {
    if (this.pasoActual > 1) {
      this.pasoActual--;
      this.errorMessage = '';
    }
  }

  validarDatosTarjeta(): boolean {
    return this.datosPago.nombreTitular !== '' &&
           this.datosPago.numeroTarjeta.length === 16 &&
           this.datosPago.fechaVencimiento !== '' &&
           this.datosPago.cvv.length === 3;
  }

  procesarCompra(): void {
    if (!this.evento?.id) return;

    this.procesando = true;
    this.errorMessage = '';

    const request: ReservaRequest = {
      eventoId: this.evento.id,
      cantidadEntradas: this.cantidadEntradas
    };

    setTimeout(() => {
      if (this.tipoOperacion === 'compra') {
        this.reservaService.crear(request).subscribe({
          next: () => {
            this.pasoActual = 3;
            this.procesando = false;
          },
          error: (error) => {
            this.errorMessage = error.error?.message || 'Error al procesar la compra';
            this.procesando = false;
          }
        });
      } else {
        this.reservaService.crearReservaPendiente(request).subscribe({
          next: () => {
            this.pasoActual = 3;
            this.procesando = false;
          },
          error: (error) => {
            this.errorMessage = error.error?.message || 'Error al procesar la reserva';
            this.procesando = false;
          }
        });
      }
    }, 2000);
  }

  irAMisReservas(): void {
    this.router.navigate(['/mis-reservas']);
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

  formatearTarjeta(): string {
    if (this.datosPago.numeroTarjeta.length >= 4) {
      return '**** **** **** ' + this.datosPago.numeroTarjeta.slice(-4);
    }
    return '';
  }
}