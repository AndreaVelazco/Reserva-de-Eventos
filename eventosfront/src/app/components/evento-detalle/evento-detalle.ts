import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { EventoService } from '../../services/evento';
import { AuthService } from '../../services/auth';
import { Evento } from '../../models/evento.model';

@Component({
  selector: 'app-evento-detalle',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './evento-detalle.html',
  styleUrl: './evento-detalle.css'
})
export class EventoDetalleComponent implements OnInit {
  evento: Evento | null = null;
  loading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventoService: EventoService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.cargarEvento(id);
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
        this.loading = false;
        this.router.navigate(['/eventos']);
      }
    });
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
  comprar(): void {
  if (this.evento?.id) {
    this.router.navigate(['/reserva', this.evento.id], { 
      queryParams: { tipo: 'compra' } 
    });
  }
}

reservar(): void {
  if (this.evento?.id) {
    this.router.navigate(['/reserva', this.evento.id], { 
      queryParams: { tipo: 'reserva' } 
    });
  }
}
}
