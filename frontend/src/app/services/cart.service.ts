import { Injectable, signal, computed } from '@angular/core';
import { CartItem } from '../models/cart-item.model';
import { ProductoMenu } from '../models/producto-menu.model';
import { Restaurante } from '../models/restaurante.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly _items = signal<CartItem[]>([]);
  private readonly _restaurante = signal<Restaurante | null>(null);

  readonly items = this._items.asReadonly();
  readonly restaurante = this._restaurante.asReadonly();

  readonly total = computed(() =>
    this._items().reduce((sum, item) => sum + item.producto.precio * item.cantidad, 0)
  );

  readonly isEmpty = computed(() => this._items().length === 0);

  add(producto: ProductoMenu): void {
    const current = this._items();
    const existing = current.find(i => i.producto.id === producto.id);
    if (existing) {
      this._items.set(current.map(i =>
        i.producto.id === producto.id ? { ...i, cantidad: i.cantidad + 1 } : i
      ));
    } else {
      this._items.set([...current, { producto, cantidad: 1 }]);
    }
  }

  setRestaurante(restaurante: Restaurante): void {
    this._restaurante.set(restaurante);
  }

  clear(): void {
    this._items.set([]);
    this._restaurante.set(null);
  }
}
