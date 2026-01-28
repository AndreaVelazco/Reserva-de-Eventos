import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent implements OnInit {
  title = 'eventosfront';
  sidebarOpen: boolean = true;
  mostrarNavbar: boolean = true;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Escuchar cambios de ruta
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.verificarRuta();
    });

    // Verificar ruta inicial
    this.verificarRuta();

    // Escuchar toggle del sidebar
    window.addEventListener('sidebarToggle', (event: any) => {
      this.sidebarOpen = event.detail.isOpen;
    });
  }

  verificarRuta(): void {
    const rutasOcultas = ['/login', '/register'];
    this.mostrarNavbar = !rutasOcultas.includes(this.router.url);
  }
}