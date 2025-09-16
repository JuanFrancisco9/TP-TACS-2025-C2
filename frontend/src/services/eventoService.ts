import axios from 'axios';
import type {Inscripcion} from "../types/inscripciones.ts";

// Interface para la respuesta del backend
interface ResultadoBusquedaEvento {
  eventos: Evento[];
  siguientePagina: number;
  totalElementos: number;
  totalPaginas: number;
}

// Interface para el evento del frontend (estructura que usamos en la UI)
export interface Evento {
  id: string;
  descripcion: string;
  fecha: string;
  horaInicio: string;
  ubicacion: Ubicacion;
  titulo: string;
  // ...existing code...
  organizador: {
    id: string;
    nombre: string;
    apellido: string;
    dni: string;
    usuario: any;
  };
  estado: {
    fechaCambio: string;
    id: string;
    tipoEstado: string;
  };
  categoria: {
    tipo: string;
  };
  duracion: number;
  cupoMinimo: number;
  cupoMaximo: number;
  precio: Precio;
  etiquetas: string[];
  imagen: string;
}

export interface Precio {
  moneda: string;
  cantidad: number;
}

export interface Ubicacion {
  latitud: string;
  longitud: string;
  localidad: string;
  direccion: string;
}

// Service para manejar eventos
export class EventoService {
  // URL base del backend
  private static readonly BASE_URL = 'http://localhost:8080';
  
  // Configuración de axios
  private static readonly api = axios.create({
    baseURL: this.BASE_URL,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json',
    },
    auth: {
        username: "usuario",
        password: "usuario"
    }
  });

  // Método para obtener todos los eventos (usando buscarEventos)
  static async obtenerEventos(pagina: number = 0): Promise<{eventos: Evento[], totalPaginas: number, totalElementos: number}> {
    try {
      const url = `/eventos?palabrasClave=&nroPagina=${pagina}`;
      console.log('URL completa:', `${this.BASE_URL}${url}`);
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      
      console.log('�� Data completa:', response.data);
      response.data.eventos[0].imagen = "https://www.clarin.com/img/2023/11/01/EsW43ik1T_1256x620__1.jpg";
      response.data.eventos[1].imagen = "https://www.clarin.com/img/2023/11/01/EsW43ik1T_2000x1500__1.jpg";
      return {
        eventos: response.data.eventos,
        totalPaginas: response.data.totalPaginas,
        totalElementos: response.data.totalElementos
      };
    } catch (error) {
      console.error('🚨 Error:', error);
      
      if (axios.isAxiosError(error)) {
        console.error('📦 Data de error:', error.response?.data);
  
      }
      
      throw new Error('Error al cargar los eventos');
    }
  }

  // Método para obtener un evento por ID
  static async obtenerEventoPorId(id: string): Promise<Evento | null> {
    try {
      console.log('🔍 EventoService.obtenerEventoPorId - Buscando evento ID:', id);
      const response = await this.api.get<Evento>(`/eventos/${id}`);
      console.log('✅ Evento encontrado:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ Error obteniendo evento:', error);
      return null;
    }
  }

  // Método para buscar eventos (con paginación)
  static async buscarEventos(termino: string, pagina: number = 0): Promise<{eventos: Evento[], totalPaginas: number, totalElementos: number}> {
    try {
      const url = `/eventos?palabrasClave=${encodeURIComponent(termino)}&nroPagina=${pagina}`;
      console.log('🔍 EventoService.buscarEventos - Buscando:', termino);
      console.log('📍 URL:', `${this.BASE_URL}${url}`);
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      console.log('✅ Búsqueda exitosa:', response.data);
      
      return {
        eventos: response.data.eventos,
        totalPaginas: response.data.totalPaginas,
        totalElementos: response.data.totalElementos
      };
    } catch (error) {
      console.error('❌ Error buscando eventos:', error);
      throw new Error('Error al buscar eventos');
    }
  }

  // Método para filtrar por categoría (con paginación)
  static async filtrarPorCategoria(categoria: string, pagina: number = 0): Promise<{eventos: Evento[], totalPaginas: number, totalElementos: number}> {
    try {
      const url = `/eventos?categoria=${encodeURIComponent(categoria)}&palabrasClave=&nroPagina=${pagina}`;
      console.log('🔍 EventoService.filtrarPorCategoria - Categoría:', categoria);
      console.log('📍 URL:', `${this.BASE_URL}${url}`);
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      console.log('✅ Filtro exitoso:', response.data);
      
      return {
        eventos: response.data.eventos,
        totalPaginas: response.data.totalPaginas,
        totalElementos: response.data.totalElementos
      };
    } catch (error) {
      console.error('❌ Error filtrando eventos:', error);
      throw new Error('Error al filtrar eventos');
    }
  }

  // Método para inscribirse a un evento
  static async inscribirseAEvento(eventoId: string): Promise<boolean> {
    try {
      console.log('🔍 EventoService.inscribirseAEvento - Evento ID:', eventoId);
      // TODO: Obtener de algun lado la info del participante logueado
      const body = { 
        participante:{ 
          id: '1',
          nombre: 'Carlos',
          apellido: 'López',
          dni: '12345678',
          usuario: { id: '1', username: 'carlos', password: 'carlos'}
        },
        evento_id: "0" 
      };

      await this.api.post(`/inscripciones`, body);
      console.log('✅ Inscripción exitosa');
      return true;
    } catch (error) {
      console.error('❌ Error inscribiéndose al evento:', error);
      throw new Error('Error al inscribirse al evento');
    }
  }

  // Método para crear un evento (para administradores)
  static async crearEvento(evento: Omit<Evento, 'id'>): Promise<Evento> {
    try {
      console.log('�� EventoService.crearEvento - Creando evento:', evento);
      const response = await this.api.post<Evento>('/eventos', evento);
      console.log('✅ Evento creado:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ Error creando evento:', error);
      throw new Error('Error al crear el evento');
    }
  }

  // Método para actualizar un evento (para administradores)
  static async actualizarEvento(id: string, evento: Partial<Evento>): Promise<Evento> {
    try {
      console.log('🔍 EventoService.actualizarEvento - Actualizando evento ID:', id);
      console.log(evento)
      const response = await this.api.put<Evento>(`/eventos/${id}`, evento);
      console.log('✅ Evento actualizado:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ Error actualizando evento:', error);
      throw new Error('Error al actualizar el evento');
    }
  }

  // Método para eliminar un evento (para administradores)
  static async eliminarEvento(id: string): Promise<boolean> {
    try {
      console.log('�� EventoService.eliminarEvento - Eliminando evento ID:', id);
      await this.api.delete(`/eventos/${id}`);
      console.log('✅ Evento eliminado');
      return true;
    } catch (error) {
      console.error('❌ Error eliminando evento:', error);
      throw new Error('Error al eliminar el evento');
    }
  }

  // Método para obtener eventos por etiquetas (con paginación)
  static async obtenerEventosPorEtiquetas(etiquetas: string[], pagina: number = 0): Promise<{eventos: Evento[], totalPaginas: number, totalElementos: number}> {
    try {
      const terminoBusqueda = etiquetas.join(' ');
      const url = `/eventos?palabrasClave=${encodeURIComponent(terminoBusqueda)}&nroPagina=${pagina}`;
      console.log('🔍 EventoService.obtenerEventosPorEtiquetas - Etiquetas:', etiquetas);
      console.log('📍 URL:', `${this.BASE_URL}${url}`);
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      console.log('✅ Búsqueda por etiquetas exitosa:', response.data);
      
      return {
        eventos: response.data.eventos,
        totalPaginas: response.data.totalPaginas,
        totalElementos: response.data.totalElementos
      };
    } catch (error) {
      console.error('❌ Error obteniendo eventos por etiquetas:', error);
      throw new Error('Error al obtener eventos por etiquetas');
    }
  }

  // Método para obtener eventos por estado (con paginación)
  static async obtenerEventosPorEstado(estado: string, pagina: number = 0): Promise<{eventos: Evento[], totalPaginas: number, totalElementos: number}> {
    try {
      const url = `/eventos?palabrasClave=${encodeURIComponent(estado)}&nroPagina=${pagina}`;
      console.log('🔍 EventoService.obtenerEventosPorEstado - Estado:', estado);
      console.log('📍 URL:', `${this.BASE_URL}${url}`);
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      console.log('✅ Búsqueda por estado exitosa:', response.data);
      
      return {
        eventos: response.data.eventos,
        totalPaginas: response.data.totalPaginas,
        totalElementos: response.data.totalElementos
      };
    } catch (error) {
      console.error('❌ Error obteniendo eventos por estado:', error);
      throw new Error('Error al obtener eventos por estado');
    }
  }

  static async obtenerWaitlistDeEvento(evento: Evento | null): Promise<Inscripcion[]> {
      try{
          const url = `/waitlist/${evento?.id}`
          const response = await this.api.get(url)
          return response.data.inscripcionesSinConfirmar
      }catch (error){
          console.log(error)
          throw new Error('Error al obtener waitlist');
      }
  }

  // Método para buscar eventos con filtros avanzados
  static async buscarEventosConFiltros(filtros: {
    palabrasClave?: string;
    categoria?: string;
    ubicacion?: string;
    fechaInicio?: string;
    fechaFin?: string;
    precioMin?: number;
    precioMax?: number;
    pagina?: number;
  }): Promise<{eventos: Evento[], totalPaginas: number, totalElementos: number}> {
    try {
      const params = new URLSearchParams();
      
      if (filtros.palabrasClave) params.append('palabrasClave', filtros.palabrasClave);
      if (filtros.categoria) params.append('categoria', filtros.categoria);
      if (filtros.ubicacion) params.append('ubicacion', filtros.ubicacion);
      if (filtros.fechaInicio) params.append('fechaInicio', filtros.fechaInicio);
      if (filtros.fechaFin) params.append('fechaFin', filtros.fechaFin);
      if (filtros.precioMin !== undefined) params.append('precioMin', filtros.precioMin.toString());
      if (filtros.precioMax !== undefined) params.append('precioMax', filtros.precioMax.toString());
      
      params.append('nroPagina', (filtros.pagina || 0).toString());

      const url = `/eventos?${params.toString()}`;
      console.log('🔍 EventoService.buscarEventosConFiltros - Filtros:', filtros);
      console.log('📍 URL:', `${this.BASE_URL}${url}`);
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      console.log('✅ Búsqueda con filtros exitosa:', response.data);
      
      return {
        eventos: response.data.eventos,
        totalPaginas: response.data.totalPaginas,
        totalElementos: response.data.totalElementos
      };
    } catch (error) {
      console.error('❌ Error buscando eventos con filtros:', error);
      throw new Error('Error al buscar eventos con filtros');
    }
  }
}