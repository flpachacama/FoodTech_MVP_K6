import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { OrderRequest } from '../models/order-request.model';
import { Clima } from '../models/order-request.model';
import { OrderResponse } from '../models/order-response.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.orderServiceUrl}/orders`;

  private readonly CLIMAS: Clima[] = ['SOLEADO', 'LLUVIA_SUAVE', 'LLUVIA_FUERTE'];

  getRandomClima(): Clima {
    return this.CLIMAS[Math.floor(Math.random() * this.CLIMAS.length)];
  }

  crearPedido(order: OrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(this.baseUrl, order).pipe(
      catchError(this.handleError)
    );
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
