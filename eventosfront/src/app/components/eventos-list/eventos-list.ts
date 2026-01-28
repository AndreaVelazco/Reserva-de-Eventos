import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { EventoService } from '../../services/evento';
import { AuthService } from '../../services/auth';
import { Evento } from '../../models/evento.model';

@Component({
  selector: 'app-eventos-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './eventos-list.html',
  styleUrl: './eventos-list.css'
})
export class EventosListComponent implements OnInit {
  eventos: Evento[] = [];
  loading: boolean = true;

  constructor(
    private eventoService: EventoService,
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarEventos();
  }

  cargarEventos(): void {
    this.eventoService.obtenerTodos().subscribe({
      next: (data) => {
        this.eventos = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar eventos:', error);
        this.loading = false;
      }
    });
  }

  verDetalle(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/eventos', id]);
    }
  }

  editarEvento(id: number | undefined, event: Event): void {
    event.stopPropagation();
    if (id) {
      this.router.navigate(['/evento-form', id]);
    }
  }

  eliminarEvento(id: number | undefined, event: Event): void {
    event.stopPropagation();
    if (id && confirm('¿Está seguro de eliminar este evento?')) {
      this.eventoService.eliminar(id).subscribe({
        next: () => {
          this.cargarEventos();
        },
        error: (error) => {
          console.error('Error al eliminar evento:', error);
          alert('Error al eliminar el evento');
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
}