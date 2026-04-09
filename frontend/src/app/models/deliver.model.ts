export interface Deliver {
  id: number;
  nombre: string;
  estado: 'ACTIVO' | 'INACTIVO' | 'EN_ENTREGA';
  vehiculo: 'BICICLETA' | 'MOTO' | 'AUTO';
  ubicacionX: number;
  ubicacionY: number;
}
