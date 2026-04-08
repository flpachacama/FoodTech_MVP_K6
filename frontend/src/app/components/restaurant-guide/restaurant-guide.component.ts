import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-restaurant-guide',
  standalone: true,
  templateUrl: './restaurant-guide.component.html',
  styleUrls: ['./restaurant-guide.component.css']
})
export class RestaurantGuideComponent {
  @Input() message = 'Para seleccionar un restaurante, haz clic en nuestras opciones, elige el menú y realiza tu pedido.';
}
