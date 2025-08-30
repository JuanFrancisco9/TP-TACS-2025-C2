package org.utn.ba.tptacsg2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EstadisticasUso(
    @JsonProperty("cantidad_eventos")
    Integer cantidadEventos,

    @JsonProperty("cantidad_eventos_activos")
    Integer cantidadEventosActivos,

    @JsonProperty("cantidad_inscripciones_totales")
    Integer cantidadInscripcionesTotales,

    @JsonProperty("cantidad_inscripciones_confirmadas")
    Integer cantidadInscripcionesConfirmadas,

    @JsonProperty("cantidad_inscripciones_waitlist")
    Integer cantidadInscripcionesWaitlist,

    @JsonProperty("tasa_conversion_waitlist")
    Double tasaConversionWaitlist,

    @JsonProperty("evento_mas_popular")
    String eventoMasPopular,

    @JsonProperty("promedio_inscripciones_por_evento")
    Double promedioInscripcionesPorEvento
) {}

