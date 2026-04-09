import { ProductoMenu } from './producto-menu.model';

export interface OrderResponse {
  id: number;
  restauranteId: number;
  repartidorId: number;
  productos: ProductoMenu[];
  clienteId: number;
  clienteNombre: string;
  clienteCoordenadasX: number;
  clienteCoordenadasY: number;
  clienteTelefono: string;
  tiempoEstimado: number;
  estado: string;
}
