import { Component, Input, Output, EventEmitter, inject, signal, OnChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { ActiveOrdersService } from '../../services/active-orders.service';
import { OrderRequest } from '../../models/order-request.model';
import { OrderResponse } from '../../models/order-response.model';
import { MOCK_USER, Favorito } from '../../data/mock-user';

@Component({
  selector: 'app-order-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './order-form-modal.component.html',
  styleUrls: ['./order-form-modal.component.css']
})
export class OrderFormModalComponent implements OnChanges, OnDestroy {
  @Input() visible = false;
  @Output() close = new EventEmitter<void>();
  @Output() pedidoConfirmado = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  public readonly cartService = inject(CartService);
  private readonly orderService = inject(OrderService);
  private readonly activeOrdersService = inject(ActiveOrdersService);
  private readonly destroy$ = new Subject<void>();

  enviando = signal(false);
  error = signal<string | null>(null);

  readonly mockUser = MOCK_USER;
  favoritoSeleccionado = signal<Favorito>(MOCK_USER.favoritos[0]);

  form: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(2)]],
    telefono: ['', [Validators.required, Validators.pattern(/^[0-9]+$/), Validators.minLength(7)]],
    ubicacionX: [null, [Validators.required]],
    ubicacionY: [null, [Validators.required]]
  });

  ngOnChanges(): void {
    if (this.visible) {
      this.aplicarFavorito(MOCK_USER.favoritos[0]);
      this.favoritoSeleccionado.set(MOCK_USER.favoritos[0]);
      this.error.set(null);
    }
  }

  onFavoritoChange(alias: string): void {
    const favorito = MOCK_USER.favoritos.find(f => f.alias === alias);
    if (favorito) {
      this.favoritoSeleccionado.set(favorito);
      this.aplicarFavorito(favorito);
    }
  }

  private aplicarFavorito(favorito: Favorito): void {
    this.form.patchValue({
      nombre: MOCK_USER.nombre,
      telefono: MOCK_USER.telefono,
      ubicacionX: favorito.coordenadaX,
      ubicacionY: favorito.coordenadaY
    });
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.onClose();
    }
  }

  onClose(): void {
    this.close.emit();
  }

  getControl(name: string): AbstractControl {
    return this.form.get(name)!;
  }

  isInvalid(name: string): boolean {
    const ctrl = this.getControl(name);
    return ctrl.invalid && ctrl.touched;
  }

  onConfirmar(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    const restaurante = this.cartService.restaurante();
    if (!restaurante) return;
        

    const productos = this.cartService.items().flatMap(item =>
      Array.from({ length: item.cantidad }, () => item.producto)
    );

    const { nombre, telefono, ubicacionX, ubicacionY } = this.form.value;

    const order: OrderRequest = {
      restauranteId: restaurante.id,
      restauranteX: restaurante.coordenadaX,
      restauranteY: restaurante.coordenadaY,
      clima: this.orderService.getRandomClima(),
      productos,
      clienteId: Math.floor(Math.random() * 9000) + 1000,
      clienteNombre: nombre,
      clienteCoordenadasX: Number(ubicacionX),
      clienteCoordenadasY: Number(ubicacionY),
      clienteTelefono: telefono
    };
    this.enviando.set(true);
    this.error.set(null);

    this.orderService.crearPedido(order)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
      next: (response: OrderResponse) => {
        this.enviando.set(false);
        this.activeOrdersService.add(response);
        this.pedidoConfirmado.emit();
      },
      error: () => {
        this.enviando.set(false);
        this.error.set('No se pudo crear el pedido. Intenta de nuevo.');
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
