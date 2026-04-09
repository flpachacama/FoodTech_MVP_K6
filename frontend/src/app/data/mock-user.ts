export interface Favorito {
  alias: string;
  direccion: string;
  coordenadaX: number;
  coordenadaY: number;
}

export interface MockUser {
  nombre: string;
  telefono: string;
  favoritos: Favorito[];
}

export const MOCK_USER: MockUser = {
  nombre: 'Carlos Mendoza',
  telefono: '3001234567',
  favoritos: [
    {
      alias: 'Casa',
      direccion: 'Cra 7 #45-20, Chapinero',
      coordenadaX: -74.0637,
      coordenadaY: 4.6482
    },
    {
      alias: 'Trabajo',
      direccion: 'Cl 72 #10-34, El Chicó',
      coordenadaX: -74.0503,
      coordenadaY: 4.6643
    },
    {
      alias: 'Novia',
      direccion: 'Av 19 #116-30, Usaquén',
      coordenadaX: -74.0533,
      coordenadaY: 4.6942
    }
  ]
};
