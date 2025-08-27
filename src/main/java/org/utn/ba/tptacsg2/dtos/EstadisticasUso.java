package org.utn.ba.tptacsg2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EstadisticasUso(
    @JsonProperty("cantidad_eventos")
    Long cantidadEventos,

    @JsonProperty("cantidad_eventos_activos")
    Long cantidadEventosActivos,

    @JsonProperty("cantidad_inscripciones_totales")
    Long cantidadInscripcionesTotales,

    @JsonProperty("cantidad_inscripciones_confirmadas")
    Long cantidadInscripcionesConfirmadas,

    @JsonProperty("cantidad_inscripciones_waitlist")
    Long cantidadInscripcionesWaitlist,

    @JsonProperty("tasa_conversion_waitlist")
    Double tasaConversionWaitlist,

    @JsonProperty("evento_mas_popular")
    String eventoMasPopular,

    @JsonProperty("promedio_inscripciones_por_evento")
    Double promedioInscripcionesPorEvento
) {}

