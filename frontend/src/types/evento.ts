import type {Organizador} from "./auth.ts";

export interface ResultadoBusquedaEvento {
    eventos: Evento[];
    siguientePagina: number;
    totalElementos: number;
    totalPaginas: number;
}

export interface Evento {
    id: string;
    descripcion: string;
    fecha: string;
    horaInicio: string;
    ubicacion: Ubicacion;
    titulo: string;
    organizador: Organizador;
    estado: EstadoEvento;
    categoria: {
        tipo: string;
        icono?: string;
    };
    duracion: number;
    cupoMinimo: number;
    cupoMaximo: number;
    precio: Precio;
    etiquetas: string[];
    imagen?: string;
    imagenUrl?: string;
    imagenKey?: string;
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

interface EstadoEvento {
    fechaCambio: string;
    id: string;
    tipoEstado: string;
}

export interface CategoriaDTO {
    tipo: string;
    icono?: string;
}

export interface CategoriaIconRule {
    icono: string;
    keywords: string[];
}
