import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { OrderResponse } from '../models/order-response.model';
import { DeliverOrderResponse } from '../models/deliver-order-response.model';

@Injectable({
  providedIn: 'root'
})
export class RepartidorOrderService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.orderServiceUrl}/orders`;

  getOrderByRepartidorId(id: number): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.baseUrl}/repartidor/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  deliver(pedidoId: number): Observable<DeliverOrderResponse> {
    return this.http.put<DeliverOrderResponse>(`${this.baseUrl}/${pedidoId}/deliver`, {}).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    return throwError(() => error);
  }
}
