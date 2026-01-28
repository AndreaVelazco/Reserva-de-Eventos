import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { EventoService } from '../../services/evento';
import { Evento } from '../../models/evento.model';

interface CalendarioEvento extends Evento {
  color?: string;
}

interface MesInfo {
  numero: number;
  nombre: string;
  cantidadEventos: number;
  eventos: CalendarioEvento[];
}

@Component({
  selector: 'app-calendario',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './calendario.html',
  styleUrl: './calendario.css'
})
export class CalendarioComponent implements OnInit {
  eventos: CalendarioEvento[] = [];
  meses: MesInfo[] = [];
  anioActual: number = new Date().getFullYear();
  mesSeleccionado: MesInfo | null = null;
  vistaActual: 'meses' | 'eventos' = 'meses';
  loading: boolean = true;
  colores: string[] = [
    '#667eea', '#f093fb', '#f5576c', '#4facfe', 
    '#43e97b', '#fa709a', '#fee140', '#30cfd0',
    '#a8edea', '#fed6e3', '#c471f5', '#fa8231'
  ];

  constructor(
    private eventoService: EventoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarEventos();
  }

  cargarEventos(): void {
    this.eventoService.obtenerTodos().subscribe({
      next: (data) => {
        this.eventos = data.map((evento, index) => ({
          ...evento,
          color: this.colores[index % this.colores.length]
        }));
        this.generarMeses();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar eventos:', error);
        this.loading = false;
      }
    });
  }

  generarMeses(): void {
    const nombresMeses = [
      'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
      'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
    ];

    this.meses = nombresMeses.map((nombre, index) => {
      const eventosDelMes = this.eventos.filter(evento => {
        const fechaEvento = new Date(evento.fecha);
        return fechaEvento.getMonth() === index && 
               fechaEvento.getFullYear() === this.anioActual;
      });

      return {
        numero: index,
        nombre: nombre,
        cantidadEventos: eventosDelMes.length,
        eventos: eventosDelMes.sort((a, b) => 
          new Date(a.fecha).getTime() - new Date(b.fecha).getTime()
        )
      };
    });
  }

  seleccionarMes(mes: MesInfo): void {
    this.mesSeleccionado = mes;
    this.vistaActual = 'eventos';
  }

  volverAMeses(): void {
    this.mesSeleccionado = null;
    this.vistaActual = 'meses';
  }

  cambiarAnio(direccion: number): void {
    this.anioActual += direccion;
    this.generarMeses();
  }

  irAnioActual(): void {
    this.anioActual = new Date().getFullYear();
    this.generarMeses();
    this.volverAMeses();
  }

  verEvento(eventoId: number | undefined): void {
    if (eventoId) {
      this.router.navigate(['/eventos', eventoId]);
    }
  }

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-PE', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatearHora(fecha: string): string {
    return new Date(fecha).toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatearDia(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-PE', {
      day: 'numeric',
      weekday: 'short'
    });
  }
}