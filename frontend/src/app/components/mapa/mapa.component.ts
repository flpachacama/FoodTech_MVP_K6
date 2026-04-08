import { Component, OnInit, inject, signal, output, OnDestroy, Input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RestauranteService } from '../../services/restaurante.service';
import { Restaurante } from '../../models/restaurante.model';

@Component({
  selector: 'app-mapa',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mapa.component.html',
  styleUrls: ['./mapa.component.css']
})
export class MapaComponent implements OnInit, OnDestroy {
  private readonly restauranteService = inject(RestauranteService);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly destroy$ = new Subject<void>();

  private readonly DEFAULT_LAT = 4.6248;
  private readonly DEFAULT_LNG = -74.0863;

  restaurantes = signal<Restaurante[]>([]);
  restauranteSelected = output<Restaurante>();

  selectedRestaurante = signal<Restaurante | null>(null);

  mapUrl = computed<SafeResourceUrl>(() => {
    const r = this.selectedRestaurante();
    let lat = this.DEFAULT_LAT;
    let lng = this.DEFAULT_LNG;
    let zoom = 14;
    if (r) {
      lat = r.coordenadaY;   
      lng = r.coordenadaX;
      zoom = 16;
    }

    const url = `https://www.google.com/maps/embed/v1/place?key=&q=${lat},${lng}&zoom=${zoom}&maptype=roadmap`;
    const fallbackUrl = `https://maps.google.com/maps?q=${lat},${lng}&z=${zoom}&output=embed`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(fallbackUrl);
  });

  @Input() set restauranteInput(value: Restaurante | null) {
    this.selectedRestaurante.set(value);
  }

  ngOnInit(): void {
    this.restauranteService.getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (restaurantes) => this.restaurantes.set(restaurantes),
        error: () => {}
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
