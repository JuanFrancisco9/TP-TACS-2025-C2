export interface Usuario {
  id: string;
  nombre: string;
  apellido: string;
  dni: string;
}

export interface Participante {
  id: string;
  nombre: string;
  apellido: string;
  dni: string;
}

export interface Estado {
  tipoEstado: string;
  fechaCambio: string;
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