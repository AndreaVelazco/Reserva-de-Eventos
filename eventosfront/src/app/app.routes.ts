import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { EventosListComponent } from './components/eventos-list/eventos-list';
import { EventoDetalleComponent } from './components/evento-detalle/evento-detalle';
import { EventoFormComponent } from './components/evento-form/evento-form';
import { CalendarioComponent } from './components/calendario/calendario';
import { ReservaComponent } from './components/reserva/reserva';
import { MisReservasComponent } from './components/mis-reservas/mis-reservas';
import { authGuard } from './guards/auth-guard';
import { adminGuard } from './guards/admin-guard';
import { userGuard } from './guards/user-guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'eventos', component: EventosListComponent },
  { path: 'calendario', component: CalendarioComponent },
  { path: 'eventos/:id', component: EventoDetalleComponent },
  { path: 'evento-form', component: EventoFormComponent, canActivate: [adminGuard] },
  { path: 'evento-form/:id', component: EventoFormComponent, canActivate: [adminGuard] },
  { path: 'reserva/:eventoId', component: ReservaComponent, canActivate: [userGuard] }, // Solo usuarios
  { path: 'mis-reservas', component: MisReservasComponent, canActivate: [authGuard] }
];