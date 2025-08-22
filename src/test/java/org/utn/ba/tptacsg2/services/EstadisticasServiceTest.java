package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.utn.ba.tptacsg2.dtos.EstadisticasUso;

import static org.junit.jupiter.api.Assertions.*;

class EstadisticasServiceTest {

    private final EstadisticasService estadisticasService = new EstadisticasService();

    @Test
    @DisplayName("Debe obtener estadísticas de uso con valores esperados")
    void obtenerEstadisticasUso_DebeRetornarValoresEsperados() {

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();


        assertNotNull(resultado);
        assertEquals(15L, resultado.cantidadEventos());
        assertEquals(8L, resultado.cantidadEventosActivos());
        assertEquals(120L, resultado.cantidadInscripcionesTotales());
        assertEquals(95L, resultado.cantidadInscripcionesConfirmadas());
        assertEquals(25L, resultado.cantidadInscripcionesWaitlist());
        assertEquals("TP TACS G2 - Primer Entrega", resultado.eventoMasPopular());
    }

    @Test
    @DisplayName("Debe calcular correctamente la tasa de conversión waitlist")
    void obtenerEstadisticasUso_DebeCalcularTasaConversionCorrectamente() {

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();

        //Tasa de conversión: 15 conversiones de 25 en waitlist = 60%
        assertEquals(60.0, resultado.tasaConversionWaitlist());
    }

    @Test
    @DisplayName("Debe calcular correctamente el promedio de inscripciones por evento")
    void obtenerEstadisticasUso_DebeCalcularPromedioInscripciones() {

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();

        // Promedio: 120 inscripciones / 15 eventos = 8.0
        assertEquals(8.0, resultado.promedioInscripcionesPorEvento());
    }

    @Test
    @DisplayName("Todos los valores deben ser no nulos")
    void obtenerEstadisticasUso_TodosLosValoresDebenSerNoNulos() {

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();


        assertAll("Todos los campos deben ser no nulos",
            () -> assertNotNull(resultado.cantidadEventos()),
            () -> assertNotNull(resultado.cantidadEventosActivos()),
            () -> assertNotNull(resultado.cantidadInscripcionesTotales()),
            () -> assertNotNull(resultado.cantidadInscripcionesConfirmadas()),
            () -> assertNotNull(resultado.cantidadInscripcionesWaitlist()),
            () -> assertNotNull(resultado.tasaConversionWaitlist()),
            () -> assertNotNull(resultado.eventoMasPopular()),
            () -> assertNotNull(resultado.promedioInscripcionesPorEvento())
        );
    }

    @Test
    @DisplayName("Los valores numéricos deben ser positivos o cero")
    void obtenerEstadisticasUso_ValoresNumericosDebenSerPositivos() {

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();


        assertAll("Todos los valores numéricos deben ser >= 0",
            () -> assertTrue(resultado.cantidadEventos() >= 0),
            () -> assertTrue(resultado.cantidadEventosActivos() >= 0),
            () -> assertTrue(resultado.cantidadInscripcionesTotales() >= 0),
            () -> assertTrue(resultado.cantidadInscripcionesConfirmadas() >= 0),
            () -> assertTrue(resultado.cantidadInscripcionesWaitlist() >= 0),
            () -> assertTrue(resultado.tasaConversionWaitlist() >= 0),
            () -> assertTrue(resultado.promedioInscripcionesPorEvento() >= 0)
        );
    }

    @Test
    @DisplayName("El evento más popular no debe estar vacío")
    void obtenerEstadisticasUso_EventoMasPopularNoDebeEstarVacio() {

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();


        assertNotNull(resultado.eventoMasPopular());
        assertFalse(resultado.eventoMasPopular().isEmpty());
        assertTrue(resultado.eventoMasPopular().length() > 0);
    }
}
