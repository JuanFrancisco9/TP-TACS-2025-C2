export interface EstadisticasUsoDTO {
    cantidad_eventos: number | null;
    cantidad_eventos_activos: number | null;
    cantidad_inscripciones_totales: number | null;
    cantidad_inscripciones_confirmadas: number | null;
    cantidad_inscripciones_waitlist: number | null;
    tasa_conversion_waitlist: number | null;
    evento_mas_popular: string | null;
    promedio_inscripciones_por_evento: number | null;
}

export const TipoEstadistica = {
    CANTIDAD_EVENTOS: 'CANTIDAD_EVENTOS',
    CANTIDAD_EVENTOS_ACTIVOS: 'CANTIDAD_EVENTOS_ACTIVOS', 
    CANTIDAD_INSCRIPCIONES_TOTALES: 'CANTIDAD_INSCRIPCIONES_TOTALES',
    CANTIDAD_INSCRIPCIONES_CONFIRMADAS: 'CANTIDAD_INSCRIPCIONES_CONFIRMADAS',
    CANTIDAD_INSCRIPCIONES_WAITLIST: 'CANTIDAD_INSCRIPCIONES_WAITLIST',
    TASA_CONVERSION_WAITLIST: 'TASA_CONVERSION_WAITLIST',
    EVENTO_MAS_POPULAR: 'EVENTO_MAS_POPULAR',
    PROMEDIO_INSCRIPCIONES_POR_EVENTO: 'PROMEDIO_INSCRIPCIONES_POR_EVENTO'
} as const;

export type TipoEstadistica = typeof TipoEstadistica[keyof typeof TipoEstadistica];

export interface EstadisticasParams {
    fechaDesde?: string;
    fechaHasta?: string;
    estadisticas?: TipoEstadistica[];
}