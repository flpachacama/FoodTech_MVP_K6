import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { OrderResponse } from '../models/order-response.model';

@Injectable({ providedIn: 'root' })
export class ActiveOrdersService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.orderServiceUrl}/orders`;

  private readonly _orders = signal<OrderResponse[]>([]);

  readonly orders = this._orders.asReadonly();
  readonly hasOrders = computed(() => this._orders().length > 0);

  add(order: OrderResponse): void {
    this._orders.update(current => [...current, order]);
  }

  cancel(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${id}/cancel`, {}).pipe(
      catchError(this.handleError)
    );
  }

  remove(id: number): void {
    this._orders.update(current => current.filter(o => o.id !== id));
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Error desconocido';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      errorMessage = `Código: ${error.status}, Mensaje: ${error.message}`;
    }
    return throwError(() => new Error(errorMessage));
  }
}
