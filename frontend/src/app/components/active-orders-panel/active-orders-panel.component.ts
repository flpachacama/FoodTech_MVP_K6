import { Component, inject, signal, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ActiveOrdersService } from '../../services/active-orders.service';
import { OrderResponse } from '../../models/order-response.model';

@Component({
  selector: 'app-active-orders-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './active-orders-panel.component.html',
  styleUrls: ['./active-orders-panel.component.css']
})
export class ActiveOrdersPanelComponent implements OnDestroy {
  readonly activeOrdersService = inject(ActiveOrdersService);
  private readonly destroy$ = new Subject<void>();

  expanded = signal(true);
  cancellingId = signal<number | null>(null);

  toggle(): void {
    this.expanded.update(v => !v);
  }

  onCancel(order: OrderResponse): void {
    this.cancellingId.set(order.id);
    this.activeOrdersService.cancel(order.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
      next: () => {
        this.activeOrdersService.remove(order.id);
        this.cancellingId.set(null);
      },
      error: () => {
        this.cancellingId.set(null);
      }
    });
  }

  formatPrice(precio: number): string {
    return '$' + precio.toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  totalOrder(order: OrderResponse): number {
    return order.productos.reduce((sum, p) => sum + p.precio, 0);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
