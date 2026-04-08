import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { Deliver } from '../models';

@Injectable({
  providedIn: 'root'
})
export class DeliverService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.deliveryServiceUrl}/delivers`;

  getAll(): Observable<Deliver[]> {
    return this.http.get<Deliver[]>(this.baseUrl).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: number): Observable<Deliver> {
    return this.http.get<Deliver>(`${this.baseUrl}/${id}`).pipe(
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
