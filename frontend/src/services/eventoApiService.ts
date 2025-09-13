// Types based on backend models
export interface Ubicacion {
  direccion: string;
  ciudad: string;
  provincia: string;
}

export interface Precio {
  monto: number;
  moneda: string;
}

export interface Organizador {
  id: string;
  nombre: string;
  apellido: string;
  email: string;
}

export interface EstadoEvento {
  tipo: string;
  fechaCambio: string;
}

export interface Categoria {
  nombre: string;
  descripcion: string;
}

export interface Evento {
  id: string;
  titulo: string;
  descripcion: string;
  fecha: string;
  horaInicio: string;
  duracion: number;
  ubicacion: Ubicacion;
  cupoMaximo: number;
  cupoMinimo: number;
  precio: Precio;
  organizador: Organizador;
  estado: EstadoEvento;
  categoria: Categoria;
  etiquetas: string[];
}

export interface ResultadoBusquedaEvento {
  eventos: Evento[];
  siguientePagina: number;
  totalElementos: number;
  totalPaginas: number;
}

export interface FiltrosBusqueda {
  palabrasClave: string;
  nroPagina?: number;
}

// API service for event search
export class EventoApiService {
  private baseUrl: string;

  constructor(baseUrl: string = 'http://localhost:8080') {
    this.baseUrl = baseUrl;
  }

  async buscarEventos(filtros: FiltrosBusqueda): Promise<ResultadoBusquedaEvento> {
    try {
      const params = new URLSearchParams();
      params.append('palabrasClave', filtros.palabrasClave);
      params.append('nroPagina', (filtros.nroPagina || 1).toString());

      const response = await fetch(`${this.baseUrl}/eventos?${params.toString()}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error del servidor: ${response.status}`);
      }

      const resultado: ResultadoBusquedaEvento = await response.json();
      return resultado;
    } catch (error) {
      console.error('Error searching eventos:', error);
      throw error;
    }
  }
}

// Export a default instance
export const eventoApiService = new EventoApiService();
