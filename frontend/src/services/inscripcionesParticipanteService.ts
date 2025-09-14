import type { Inscripcion } from '../types/inscripciones';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const API_USERNAME = import.meta.env.VITE_API_USERNAME;
const API_PASSWORD = import.meta.env.VITE_API_PASSWORD;

class InscripcionesParticipanteService {
    private getAuthHeaders() {
        const credentials = btoa(`${API_USERNAME}:${API_PASSWORD}`);
        return {
            'Content-Type': 'application/json',
            'Authorization': `Basic ${credentials}`
        };
    }

    async obtenerInscripcionesDeParticipante(idParticipante: string): Promise<Inscripcion[]> {
        const url = `${API_BASE_URL}/participantes/inscripciones/${idParticipante}`;

        const response = await fetch(url, {
            method: 'GET',
            headers: this.getAuthHeaders(),
        });

        if (response.status === 204) {
            return [];
        }

        if (!response.ok) {
            throw new Error(`Error al obtener inscripciones: ${response.status} - ${response.statusText}`);
        }

        return response.json();
    }
}

export default new InscripcionesParticipanteService();