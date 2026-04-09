import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Restaurante } from '../../models/restaurante.model';
import { ProductoMenu } from '../../models/producto-menu.model';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-menu-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './menu-modal.component.html',
  styleUrls: ['./menu-modal.component.css']
})
export class MenuModalComponent {
  @Input() restaurante: Restaurante | null = null;
  @Input() visible: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() pedidoRealizado = new EventEmitter<void>();

  readonly cartService = inject(CartService);

  onClose(): void {
    this.close.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.onClose();
    }
  }

  onAgregarProducto(producto: ProductoMenu): void {
    this.cartService.add(producto);
  }

  onHacerPedido(): void {
    this.pedidoRealizado.emit();
  }

  formatPrice(precio: number): string {
    return '$' + precio.toLocaleString('es-CO', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    });
  }
}
