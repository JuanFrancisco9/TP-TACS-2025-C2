import axios from 'axios';
import type {Inscripcion} from "../types/inscripciones.ts";
import authService from "./authService.ts";
import type {Evento, ResultadoBusquedaEvento, CategoriaDTO } from "../types/evento.ts";
import type {Participante} from "../types/auth.ts";

// Service para manejar eventoss
export class EventoService {
  // URL base del backend
  private static readonly BASE_URL = import.meta.env.VITE_API_BASE_URL;

  private static getAuthHeaders() {
      return authService.getAuthHeaders();
  }
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
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      
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
      const response = await this.api.get<Evento>(`/eventos/${id}`);
      return response.data;
    } catch (error) {
      console.error('❌ Error obteniendo evento:', error);
      return null;
    }
  }

  // Método para buscar eventos (con paginación)
  static async buscarEventos(termino: string, pagina: number = 0, ubicacion?: string): Promise<{eventos: Evento[], totalPaginas: number, totalElementos: number}> {
    try {
      const params = new URLSearchParams();
      params.set("palabrasClave", termino ?? "");
      params.set("nroPagina", String(pagina));
      if (ubicacion && ubicacion.trim()) params.set("ubicacion", ubicacion.trim());
      const url = `/eventos?${params.toString()}`;
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      
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
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      
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


  // Método para crear un evento (para administradores)
  static async crearEvento(evento: Omit<Evento, 'id'>): Promise<Evento> {
    try {
      const response = await this.api.post<Evento>('/eventos', evento);
      return response.data;
    } catch (error) {
      console.error('❌ Error creando evento:', error);
      throw new Error('Error al crear el evento');
    }
  }

  // Método para actualizar un evento (para administradores)
  static async actualizarEvento(id: string, evento: Partial<Evento>): Promise<Evento> {
    try {
      const response = await axios.put<Evento>(`${this.BASE_URL}/eventos/${id}`, evento, {
          headers: this.getAuthHeaders()
      })
      return response.data;
    } catch (error) {
      console.error('❌ Error actualizando evento:', error);
      throw new Error('Error al actualizar el evento');
    }
  }

  // Método para eliminar un evento (para administradores)
  static async eliminarEvento(id: string): Promise<boolean> {
    try {
      await this.api.delete(`/eventos/${id}`);
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
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      
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
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      
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
          const url = `${this.BASE_URL}/waitlist/${evento?.id}`
          const response = await axios.get(url, {
              headers: this.getAuthHeaders()
          })
          return response.data.inscripcionesSinConfirmar
      }catch (error){
          throw new Error('Error al obtener waitlist');
      }
  }

    static async obtenerParticipantesDeEvento(evento: Evento | null): Promise<Participante[]> {
        try{
            const url = `${this.BASE_URL}/eventos/${evento?.id}/participantes`
            const response = await axios.get(url,{
                headers: this.getAuthHeaders()
            })
            return response.data
        }catch (error){
            throw new Error('Error al obtener participantes del evento');
        }
    }

    static async actualizarEstadoEvento(evento: Evento, estado: String): Promise<Evento> {
        try{
            const url = `${this.BASE_URL}/eventos/${evento?.id}?estado=${estado}`
            const response = await axios.patch(url, null, {
                headers: this.getAuthHeaders()
            });
            return response.data
        }catch (error){
            throw new Error('Error al actualizar el estado del evento');
        }
    }

    static async obtenerEventosParaOrganizador(organizadorId: string | undefined): Promise<Evento[]> {
        try{
            const url = `${this.BASE_URL}/organizadores/eventos/${organizadorId}`
            const response = await axios.get(url,{
                headers: this.getAuthHeaders()
            })
            return response.data
        }catch (error){
            throw new Error('Error al obtener eventos para organizador');
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
      
      const response = await this.api.get<ResultadoBusquedaEvento>(url);
      
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

  // Obtener lista de categorías desde el backend
  static async obtenerCategorias(): Promise<string[]> {
    try {
      const url = `/categorias`;
      const response = await this.api.get<CategoriaDTO[]>(url);
      return (response.data || []).map(c => c.tipo);
    } catch (error) {
      console.error('Error obteniendo categorías:', error);
      return [];
    }
  }

  // Inscribirse a un evento usando el usuario del localStorage
  static async inscribirseAEvento(eventoId: string): Promise<boolean> {
    try {
      const storedUser = localStorage.getItem('currentUser');
      const user = storedUser ? JSON.parse(storedUser) as { id: number; username: string; rol?: string; actorId?: string} : null;

      const participante: any = user //TODO revisar los campos vacios para eliminar
        ? {
            id: String(user.actorId),
            nombre: '',
            apellido: '',
            dni: '',
            usuario: { id: user.id, username: user.username },
          }
        : {
            nombre: '',
            apellido: '',
            dni: '',
          };
      
      const body = {
        participante,
        evento_id: String(eventoId),
      };
      await this.api.post('/inscripciones', body);
      return true;
    } catch (error) {
      console.error('Error inscribi�ndose al evento:', error);
      let message = 'Error al inscribirse al evento';
      if (axios.isAxiosError(error)) {
        const data = error.response?.data as unknown;
        if (typeof data === 'string' && data.trim().length > 0) {
          message = data;
        } else if (data && typeof data === 'object') {
          const maybeMessage = (data as { message?: string; error?: string }).message ?? (data as { message?: string; error?: string }).error;
          if (typeof maybeMessage === 'string' && maybeMessage.trim().length > 0) {
            message = maybeMessage;
          }
        }
      } else if (error instanceof Error && error.message.trim().length > 0) {
        message = error.message;
      }
      throw new Error(message);
    }
  }
}
