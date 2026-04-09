import { Component, signal, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MapaComponent } from '../mapa/mapa.component';
import { MenuModalComponent } from '../menu-modal/menu-modal.component';
import { OrderFormModalComponent } from '../order-form-modal/order-form-modal.component';
import { ActiveOrdersPanelComponent } from '../active-orders-panel/active-orders-panel.component';
import { CartService } from '../../services/cart.service';
import { RestauranteService } from '../../services/restaurante.service';
import { Restaurante } from '../../models/restaurante.model';
import { Subject, takeUntil } from 'rxjs';
@Component({
  selector: 'app-mapa-page',
  standalone: true,
  imports: [CommonModule, MapaComponent, MenuModalComponent, OrderFormModalComponent, ActiveOrdersPanelComponent],
  templateUrl: './mapa-page.component.html',
  styleUrls: ['./mapa-page.component.css']
})
export class MapaPageComponent implements OnInit, OnDestroy {
  private readonly cartService = inject(CartService);
  private readonly restauranteService = inject(RestauranteService);
  private readonly destroy$ = new Subject<void>();

  restaurantes = signal<Restaurante[]>([]);
  /** Usado solo para centrar el mapa (no abre modal) */
  mapRestaurante = signal<Restaurante | null>(null);
  /** Usado para el modal de menú */
  selectedRestaurante = signal<Restaurante | null>(null);
  menuVisible = signal(false);
  orderFormVisible = signal(false);
  sidebarOpen = signal(false);

  ngOnInit(): void {
    this.restauranteService.getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (list) => this.restaurantes.set(list),
        error: () => {}
      });
  }

  toggleSidebar(): void {
    this.sidebarOpen.update(v => !v);
  }

  closeSidebar(): void {
    this.sidebarOpen.set(false);
  }

  /** Solo centra el mapa, no abre el modal */
  onRestauranteMapFocus(restaurante: Restaurante): void {
    this.mapRestaurante.set(restaurante);
  }

  /** Abre el modal del menú (usado por el botón "Ver menú") */
  onOpenMenu(restaurante: Restaurante): void {
    this.cartService.clear();
    this.cartService.setRestaurante(restaurante);
    this.selectedRestaurante.set(restaurante);
    this.mapRestaurante.set(restaurante);
    this.menuVisible.set(true);
    this.sidebarOpen.set(false);
  }

  onCloseMenu(): void {
    this.menuVisible.set(false);
  }

  onPedidoRealizado(): void {
    this.menuVisible.set(false);
    this.orderFormVisible.set(true);
  }

  onCloseOrderForm(): void {
    this.orderFormVisible.set(false);
    this.cartService.clear();
  }

  onPedidoConfirmado(): void {
    this.orderFormVisible.set(false);
    this.cartService.clear();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
