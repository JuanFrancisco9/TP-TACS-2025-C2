// Mock data service using the same data structure as ParticipanteRepository
import type {Participante} from "../types/auth.ts";

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
