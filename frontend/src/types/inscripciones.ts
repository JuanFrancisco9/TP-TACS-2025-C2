import type {Rol} from "./auth.ts";

export interface Usuario {
    id: string;
    username: string;
    rol: Rol;
}

export interface Participante {
  id: string;
  nombre: string;
  apellido: string;
  dni: string;
}

export interface Estado {
  tipoEstado: string;
  fechaDeCambio: string;
}

export interface EventoResumen {
  id: string;
  titulo: string;
  descripcion: string;
}

export interface Inscripcion {
  id: string;
  participante: Participante;
  fechaRegistro: string;
  estado: Estado;
  evento: EventoResumen;
}