import { ProductoMenu } from './producto-menu.model';

export type Clima = 'SOLEADO' | 'LLUVIA_SUAVE' | 'LLUVIA_FUERTE';

export interface OrderRequest {
  restauranteId: number;
  restauranteX: number;
  restauranteY: number;
  clima: Clima;
  productos: ProductoMenu[];
  clienteId: number;
  clienteNombre: string;
  clienteCoordenadasX: number;
  clienteCoordenadasY: number;
  clienteTelefono: string;
}
