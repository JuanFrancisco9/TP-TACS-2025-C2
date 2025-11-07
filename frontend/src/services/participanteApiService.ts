// Mock data service using the same data structure as ParticipanteRepository
import authService from "./authService.ts";
import {getApiBaseUrl} from "../config/runtimeEnv.ts";
const API_BASE_URL = getApiBaseUrl();
export class ParticipanteApiService {
    private getAuthHeaders() {
        return authService.getAuthHeaders();
    }
    async getParticipante(id: string) {
        const url = `${API_BASE_URL}/participantes/${id}`;

        const response = await fetch(url, {
            method: 'GET',
            headers: this.getAuthHeaders(),
        });

        console.log("Respuesta del backend:", response.status);

        // Manejo de sesión expirada
        if (response.status === 401) {
            authService.handleUnauthorized('session-expired');
            throw new Error('No autorizado');
        }

        // Manejo de caso sin contenido
        if (response.status === 204) {
            console.warn("No se encontró el participante con ID:", id);
            return null;
        }

        if (!response.ok) {
            throw new Error(`Error al obtener el perfil del participante: ${response.status} - ${response.statusText}`);
        }

        return await response.json();
    }
    async updateParticipante(id: any, participante: any) {
        const url = `${API_BASE_URL}/participantes/${id}`;
        const response = await fetch(url, {
            method: "PUT",
            headers: this.getAuthHeaders(),
            body: JSON.stringify(participante),
        });

        if (response.status === 401) {
            authService.handleUnauthorized("session-expired");
            throw new Error("No autorizado");
        }

        if (!response.ok) {
            throw new Error(`Error al actualizar participante: ${response.statusText}`);
        }

        return response.json();
    }
}

// Export a default instance
export const participanteApiService = new ParticipanteApiService();
