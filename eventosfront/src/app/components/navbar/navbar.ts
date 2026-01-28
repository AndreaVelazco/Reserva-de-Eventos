import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent implements OnInit {
  currentUser: Usuario | null = null;
  sidebarOpen: boolean = true;

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
    // Emitir evento para que el contenido se ajuste
    this.emitSidebarState();
  }

  emitSidebarState(): void {
    const event = new CustomEvent('sidebarToggle', { 
      detail: { isOpen: this.sidebarOpen } 
    });
    window.dispatchEvent(event);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}