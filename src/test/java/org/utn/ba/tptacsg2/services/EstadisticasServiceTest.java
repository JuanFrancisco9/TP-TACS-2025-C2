package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.utn.ba.tptacsg2.dtos.EstadisticasUso;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.events.Ubicacion;
import org.utn.ba.tptacsg2.models.events.Precio;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcionV2;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class EstadisticasServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    private EstadisticasService estadisticasService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        estadisticasService = new EstadisticasService(eventoRepository, inscripcionRepository);
    }

    @Test
    @DisplayName("Debe obtener estadísticas de uso con valores esperados")
    void obtenerEstadisticasUso_DebeRetornarValoresEsperados() {
        // Preparar datos de prueba
        List<Evento> eventos = crearEventosDePrueba();
        List<Inscripcion> inscripciones = crearInscripcionesDePrueba();

        when(eventoRepository.getEventos()).thenReturn(eventos);
        when(inscripcionRepository.getInscripciones()).thenReturn(inscripciones);

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();

        assertNotNull(resultado);
        assertEquals(2, resultado.cantidadEventos());
        assertEquals(2, resultado.cantidadEventosActivos());
        assertEquals(3, resultado.cantidadInscripcionesTotales());
        assertEquals(2, resultado.cantidadInscripcionesConfirmadas());
        assertEquals(1, resultado.cantidadInscripcionesWaitlist());
        assertEquals("Seminario de Mocks", resultado.eventoMasPopular());
    }

    @Test
    @DisplayName("Debe calcular correctamente la tasa de conversión waitlist")
    void obtenerEstadisticasUso_DebeCalcularTasaConversionCorrectamente() {
        List<Evento> eventos = crearEventosDePrueba();
        List<Inscripcion> inscripciones = crearInscripcionesDePrueba();

        when(eventoRepository.getEventos()).thenReturn(eventos);
        when(inscripcionRepository.getInscripciones()).thenReturn(inscripciones);

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();

        // Tasa de conversión: 2 confirmadas vs 1 pendiente = 66.66%
        assertEquals(66.66, resultado.tasaConversionWaitlist());
    }

    @Test
    @DisplayName("Debe calcular correctamente el promedio de inscripciones por evento")
    void obtenerEstadisticasUso_DebeCalcularPromedioInscripciones() {
        List<Evento> eventos = crearEventosDePrueba();
        List<Inscripcion> inscripciones = crearInscripcionesDePrueba();

        when(eventoRepository.getEventos()).thenReturn(eventos);
        when(inscripcionRepository.getInscripciones()).thenReturn(inscripciones);

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();

        // Promedio: 3 inscripciones / 2 eventos = 1.5
        assertEquals(1.5, resultado.promedioInscripcionesPorEvento());
    }

    @Test
    @DisplayName("Todos los valores deben ser no nulos")
    void obtenerEstadisticasUso_TodosLosValoresDebenSerNoNulos() {
        List<Evento> eventos = crearEventosDePrueba();
        List<Inscripcion> inscripciones = crearInscripcionesDePrueba();

        when(eventoRepository.getEventos()).thenReturn(eventos);
        when(inscripcionRepository.getInscripciones()).thenReturn(inscripciones);

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
        List<Evento> eventos = crearEventosDePrueba();
        List<Inscripcion> inscripciones = crearInscripcionesDePrueba();

        when(eventoRepository.getEventos()).thenReturn(eventos);
        when(inscripcionRepository.getInscripciones()).thenReturn(inscripciones);

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
        List<Evento> eventos = crearEventosDePrueba();
        List<Inscripcion> inscripciones = crearInscripcionesDePrueba();

        when(eventoRepository.getEventos()).thenReturn(eventos);
        when(inscripcionRepository.getInscripciones()).thenReturn(inscripciones);

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();

        assertNotNull(resultado.eventoMasPopular());
        assertFalse(resultado.eventoMasPopular().isEmpty());
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando no hay eventos")
    void obtenerEstadisticasUso_SinEventos() {
        when(eventoRepository.getEventos()).thenReturn(List.of());
        when(inscripcionRepository.getInscripciones()).thenReturn(List.of());

        EstadisticasUso resultado = estadisticasService.obtenerEstadisticasUso();

        assertEquals(0, resultado.cantidadEventos());
        assertEquals(0, resultado.cantidadEventosActivos());
        assertEquals(0, resultado.cantidadInscripcionesTotales());
        assertEquals("No hay eventos disponibles", resultado.eventoMasPopular());
        assertEquals(0.0, resultado.promedioInscripcionesPorEvento());
    }

    private List<Evento> crearEventosDePrueba() {
        Organizador organizador = new Organizador("1", "Juan", "Pérez", "12345678");

        Evento evento1 = new Evento("1", "Seminario de Mocks", "Mocks", LocalDateTime.now(),
                "19:00", 5F, new Ubicacion("", "", "", ""), 10,0,
                new Precio("Pesos", 100F), organizador,
                new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), null,new ArrayList<>());

        Evento evento2 = new Evento("2", "Workshop de Testing", "Testing", LocalDateTime.now(),
                "14:00", 3F, new Ubicacion("", "", "", ""), 15,0,
                new Precio("Pesos", 150F), organizador,
                new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), null,new ArrayList<>());

        return Arrays.asList(evento1, evento2);
    }

    private List<Inscripcion> crearInscripcionesDePrueba() {
        Participante participante1 = new Participante("1", "Carlos", "López", "11111111");
        Participante participante2 = new Participante("2", "Ana", "Martínez", "22222222");
        Participante participante3 = new Participante("3", "Luis", "García", "33333333");

        Organizador organizador = new Organizador("1", "Juan", "Pérez", "12345678");
        Evento evento1 = new Evento("1", "Seminario de Mocks", "Mocks", LocalDateTime.now(),
                "19:00", 5F, new Ubicacion("", "", "", ""), 10, 0,
                new Precio("Pesos", 100F), organizador,
                new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), null, new ArrayList<>());

        Evento evento2 = new Evento("2", "Workshop de Testing", "Testing", LocalDateTime.now(),
                "14:00", 3F, new Ubicacion("", "", "", ""), 15,0,
                new Precio("Pesos", 150F), organizador,
                new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), null,new ArrayList<>());

        EstadoInscripcionV2 estadoAceptada = new EstadoInscripcionV2("1",TipoEstadoInscripcion.ACEPTADA, null, LocalDateTime.now());
        EstadoInscripcionV2 estadoPendiente = new EstadoInscripcionV2("1",TipoEstadoInscripcion.PENDIENTE, null, LocalDateTime.now());

        Inscripcion inscripcion1 = new Inscripcion("1", participante1, LocalDateTime.now(), estadoAceptada, evento1);
        Inscripcion inscripcion2 = new Inscripcion("2", participante2, LocalDateTime.now(), estadoAceptada, evento1);
        Inscripcion inscripcion3 = new Inscripcion("3", participante3, LocalDateTime.now(), estadoPendiente, evento2);

        estadoAceptada.setInscripcion(inscripcion1);
        estadoAceptada.setInscripcion(inscripcion2);
        estadoPendiente.setInscripcion(inscripcion3);

        return Arrays.asList(inscripcion1, inscripcion2, inscripcion3);
    }
}
