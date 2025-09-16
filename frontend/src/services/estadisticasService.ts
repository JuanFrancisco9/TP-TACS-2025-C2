import type {EstadisticasUsoDTO, EstadisticasParams} from '../types/estadisticas';
import authService from './authService';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

class EstadisticasService {
    private getAuthHeaders() {
        return authService.getAuthHeaders();
    }

    async obtenerEstadisticasCompletas(params?: EstadisticasParams): Promise<EstadisticasUsoDTO> {
        const urlParams = new URLSearchParams();
        
        if (params?.fechaDesde) {
            urlParams.append('fechaDesde', params.fechaDesde);
        }
        if (params?.fechaHasta) {
            urlParams.append('fechaHasta', params.fechaHasta);
        }

        const url = `${API_BASE_URL}/estadisticas/completas${urlParams.toString() ? '?' + urlParams.toString() : ''}`;
        
        const response = await fetch(url, {
            method: 'GET',
            headers: this.getAuthHeaders(),
        });

        if (!response.ok) {
            throw new Error(`Error al obtener estadísticas: ${response.status} - ${response.statusText}`);
        }

        return response.json();
    }

    async obtenerEstadisticasPersonalizadas(params: EstadisticasParams): Promise<EstadisticasUsoDTO> {
        const urlParams = new URLSearchParams();
        
        if (params.fechaDesde) {
            urlParams.append('fechaDesde', params.fechaDesde);
        }
        if (params.fechaHasta) {
            urlParams.append('fechaHasta', params.fechaHasta);
        }
        if (params.estadisticas && params.estadisticas.length > 0) {
            params.estadisticas.forEach(stat => {
                urlParams.append('estadisticas', stat);
            });
        }

        const url = `${API_BASE_URL}/estadisticas${urlParams.toString() ? '?' + urlParams.toString() : ''}`;
        
        const response = await fetch(url, {
            method: 'GET',
            headers: this.getAuthHeaders(),
        });

        if (!response.ok) {
            throw new Error(`Error al obtener estadísticas: ${response.status} - ${response.statusText}`);
        }

        return response.json();
    }
}

export default new EstadisticasService();