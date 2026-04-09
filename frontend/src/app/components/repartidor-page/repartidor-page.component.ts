import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DeliverService } from '../../services/deliver.service';
import { RepartidorOrderService } from '../../services/repartidor-order.service';
import { Deliver } from '../../models/deliver.model';
import { OrderResponse } from '../../models/order-response.model';

@Component({
  selector: 'app-repartidor-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './repartidor-page.component.html',
  styleUrls: ['./repartidor-page.component.css']
})
export class RepartidorPageComponent implements OnInit {
  private readonly deliverService = inject(DeliverService);
  private readonly repartidorOrderService = inject(RepartidorOrderService);

  readonly REPARTIDOR_ID = 1;

  isLoading = signal(true);
  isDelivering = signal(false);
  repartidor = signal<Deliver | null>(null);
  pedidoActivo = signal<OrderResponse | null>(null);

  readonly vehiculoEmoji: Record<string, string | undefined> = {
    BICICLETA: '🚲',
    MOTO: '🏍️',
    AUTO: '🚗'
  };

  ngOnInit(): void {
    this.deliverService.getById(this.REPARTIDOR_ID).subscribe({
      next: (data) => {
        this.repartidor.set(data);
        if (data.estado === 'EN_ENTREGA') {
          this.cargarPedidoActivo();
        } else {
          this.isLoading.set(false);
        }
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  private cargarPedidoActivo(): void {
    this.repartidorOrderService.getOrderByRepartidorId(this.REPARTIDOR_ID).subscribe({
      next: (pedido) => {
        this.pedidoActivo.set(pedido);
        this.isLoading.set(false);
      },
      error: () => {
        this.pedidoActivo.set(null);
        this.isLoading.set(false);
      }
    });
  }

  marcarEntregado(): void {
    const pedido = this.pedidoActivo();
    if (!pedido) return;

    this.isDelivering.set(true);
    this.repartidorOrderService.deliver(pedido.id).subscribe({
      next: () => {
        this.pedidoActivo.set(null);
        const rep = this.repartidor();
        if (rep) {
          this.repartidor.set({ ...rep, estado: 'ACTIVO' });
        }
        this.isDelivering.set(false);
      },
      error: () => {
        this.isDelivering.set(false);
      }
    });
  }
}
