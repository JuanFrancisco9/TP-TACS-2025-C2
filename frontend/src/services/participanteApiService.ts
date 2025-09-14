// Types for the API responses based on existing backend models
export interface Usuario {
  id: number;
  username: string;
  passwordHash: string;
  rol: string;
}

export interface Participante {
  id: string;
  nombre: string;
  apellido: string;
  dni: string;
  usuario: Usuario | null;
}
export interface Event {
    id: string;
    titulo: string;
    descripcion: string;
    fecha: string;
    horaInicio: string;
    duracion: number;
    ubicacion: {
        provincia: string;
        ciudad: string;
        direccion: string;
    };
    cupoMaximo: number;
    cupoMinimo: number;
    precio: { moneda: string; monto: number };
    organizador: Participante;
    estado: { tipoEstado: 'CONFIRMADO' | 'PENDENTE' | 'CANCELADO'; fechaCambio: string };
    categoria: { tipo: string };
    etiquetas: string[];
}
// Mock data service using the same data structure as ParticipanteRepository
export class ParticipanteApiService {
  // Mock data based on ParticipanteRepository.initializeData()
  private mockParticipantes: Participante[] = [
    {
      id: "1",
      nombre: "Carlos",
      apellido: "López", 
      dni: "11111111",
      usuario: null
    },
    {
      id: "2", 
      nombre: "Ana",
      apellido: "Martínez",
      dni: "22222222",
      usuario: null
    }
  ];

  async getParticipante(id: string): Promise<Participante> {
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const participante = this.mockParticipantes.find(p => p.id === id);
    if (!participante) {
      throw new Error('Participante no encontrado');
    }
    
    return participante;
  }

  async getAllParticipantes(): Promise<Participante[]> {
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 500));
    
    return [...this.mockParticipantes];
  }
}

// Export a default instance
export const participanteApiService = new ParticipanteApiService();
