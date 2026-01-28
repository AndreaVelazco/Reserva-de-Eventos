import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { EventoService } from '../../services/evento';
import { Evento } from '../../models/evento.model';

@Component({
  selector: 'app-evento-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './evento-form.html',
  styleUrl: './evento-form.css'
})
export class EventoFormComponent implements OnInit {
  evento: Evento = {
    nombre: '',
    descripcion: '',
    fecha: '',
    ubicacion: '',
    capacidadTotal: 0,
    precioEntrada: 0
  };
  isEditMode: boolean = false;
  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventoService: EventoService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.cargarEvento(Number(id));
    }
  }

  cargarEvento(id: number): void {
    this.eventoService.obtenerPorId(id).subscribe({
      next: (data) => {
        this.evento = data;
        // Formatear fecha para el input datetime-local
        this.evento.fecha = this.formatearFechaParaInput(data.fecha);
      },
      error: (error) => {
        console.error('Error al cargar evento:', error);
        this.router.navigate(['/eventos']);
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';

    const eventoData = { ...this.evento };

    if (this.isEditMode && this.evento.id) {
      this.eventoService.actualizar(this.evento.id, eventoData).subscribe({
        next: () => {
          this.router.navigate(['/eventos']);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Error al actualizar evento';
          this.loading = false;
        }
      });
    } else {
      this.eventoService.crear(eventoData).subscribe({
        next: () => {
          this.router.navigate(['/eventos']);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Error al crear evento';
          this.loading = false;
        }
      });
    }
  }

  formatearFechaParaInput(fecha: string): string {
    const date = new Date(fecha);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }
}
