import type { Participante } from "./auth.ts";
import type { Evento } from "./evento.ts";

export interface Estado {
  tipoEstado: string;
  fechaDeCambio: string;
}

export interface Inscripcion {
  id: string;
  participante: Participante;
  fechaRegistro: string;
  estado: Estado;
  evento: Evento;
}
