import { ProductoMenu } from './producto-menu.model';

export interface Restaurante {
  id: number;
  nombre: string;
  coordenadaX: number;
  coordenadaY: number;
  menu: ProductoMenu[];
}
