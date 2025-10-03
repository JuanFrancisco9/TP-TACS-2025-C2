package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.utn.ba.tptacsg2.dtos.FiltrosDTO;
import org.utn.ba.tptacsg2.dtos.output.ResultadoBusquedaEvento;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.events.*;
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.repositories.EstadoEventoRepository;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.OrganizadorRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private OrganizadorRepository organizadorRepository;
    @Mock
    private EstadoEventoRepository estadoEventoRepository;
    @Mock
    private InscripcionRepository inscripcionRepository;
    @Mock
    private GeneradorIDService generadorIDService;
    @Mock
    private CategoriaService categoriaService;
    @Mock
    private R2StorageService r2StorageService;

    @InjectMocks
    private EventoService eventoService;

    private Organizador organizadorMock;
    private Evento eventoSinId;
    private Evento eventoValido1;
    private Evento eventoValido2;
    private SolicitudEvento solicitudEvento;
    private static String idOrganizadorMock;
    private static String idEventoMock;

    @BeforeEach
    public void setUp() {
        idOrganizadorMock = "ORG-123";
        idEventoMock = "EV-123";
        organizadorMock = new Organizador(idOrganizadorMock, "Juan", "Perez", "78414456", new Usuario("4", "juanp", "password", Rol.ROLE_ORGANIZER));

        eventoSinId = new Evento(
                null,
                "Fiesta UTN",
                "Evento de prueba",
                LocalDateTime.of(2025, 9, 10, 20, 0),
                "20:00",
                3.5f,
                new Ubicacion("-34.6037", "-58.3816", "Av. Medrano 951, CABA", ""),
                100,
                0,
                new Precio("ARS", 5000f),
                null,
                new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.of(2025, 9, 1, 12, 0))
                , null, new ArrayList<>(), null);

        solicitudEvento = new SolicitudEvento(idOrganizadorMock,
                "Fiesta UTN",
                "Evento de prueba",
                LocalDateTime.of(2025, 9, 10, 20, 0),
                "20:00",
                3.5f,
                new Ubicacion("-34.6037", "-58.3816", "Av. Medrano 951, CABA", ""),
                100,
                0,
                new Precio("ARS", 5000f),
                TipoEstadoEvento.CONFIRMADO,
                null,
                new ArrayList<>());

        Categoria categoria1 = new Categoria("MUSICA");
        Categoria categoria2 = new Categoria("TECNOLOGIA");

        eventoValido1 = new Evento("E1", "Concierto de rock vivo", "Musica", LocalDateTime.of(2025, 9, 10, 20, 0), "20:00", 2f, new Ubicacion("", "", "La Plata", "CABA"), 100, 0, new Precio("ARS", 1000f), organizadorMock, new EstadoEvento("2", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), categoria1, new ArrayList<>(), null);
        eventoValido2 = new Evento("E2", "Charla", "Tecnologia", LocalDateTime.of(2025, 10, 10, 18, 0), "18:00", 1.5f, new Ubicacion("", "", "CABA", "CABA"), 50, 0, new Precio("ARS", 500f), organizadorMock, new EstadoEvento("3", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), categoria2, new ArrayList<>(), null);

        lenient().when(eventoRepository.getEventos()).thenReturn(Arrays.asList(eventoValido1, eventoValido2));

        ReflectionTestUtils.setField(eventoService, "tamanioPagina", 20);
    }

    @Test
    public void registrarEventoGuardaEnMemoria() {
        when(organizadorRepository.getOrganizador(idOrganizadorMock)).thenReturn(Optional.of(organizadorMock));
        when(generadorIDService.generarID()).thenReturn(idOrganizadorMock);
        doNothing().when(categoriaService).guardarCategoria(null);

        Evento resultado = eventoService.registrarEvento(solicitudEvento);
        assertEquals("Fiesta UTN", resultado.titulo());
        assertEquals(organizadorMock, resultado.organizador());

        verify(eventoRepository).guardarEvento(resultado);
    }

    @Test
    public void registrarEventoFallaPorqueElIdDelOrganizadorEsInvalido() {
        when(organizadorRepository.getOrganizador("ORG-INEXISTENTE"))
                .thenReturn(Optional.empty());

        SolicitudEvento solicitudInvalida = new SolicitudEvento(null,
                "Fiesta UTN",
                "Evento de prueba",
                LocalDateTime.of(2025, 9, 10, 20, 0),
                "20:00",
                3.5f,
                new Ubicacion("-34.6037", "-58.3816", "Av. Medrano 951, CABA", ""),
                100,
                0,
                new Precio("ARS", 5000f),
                TipoEstadoEvento.CONFIRMADO,
                null,
                new ArrayList<>());

        assertThrows(RuntimeException.class, () -> {
            eventoService.registrarEvento(solicitudInvalida);
        });

        verify(eventoRepository, org.mockito.Mockito.never()).guardarEvento(org.mockito.Mockito.any());
    }

    @Test
    public void cambiarEstadoEvento() {
        when(eventoRepository.getEvento(idEventoMock)).thenReturn(Optional.of(eventoSinId));

        TipoEstadoEvento tipoEstadoEvento = TipoEstadoEvento.CANCELADO;

        Evento resultado = eventoService.cambiarEstado(idEventoMock, tipoEstadoEvento);

        assertEquals(tipoEstadoEvento, resultado.estado().getTipoEstado());
        assertNotEquals(resultado, eventoSinId);
    }

    @Test
    public void buscarEventos_sinFiltros_devuelveTodos() {

        FiltrosDTO filtros = new FiltrosDTO(null, null, null, null, null, null, null, 0);
        ResultadoBusquedaEvento resultado = eventoService.buscarEventos(filtros);

        assertEquals(2, resultado.totalElementos());
        assertTrue(resultado.eventos().stream().anyMatch(e -> e.id().equals(eventoValido1.id())));
        assertTrue(resultado.eventos().stream().anyMatch(e -> e.id().equals(eventoValido2.id())));
    }

    @Test
    public void buscarEventos_filtraPorCategoria() {


        FiltrosDTO filtros = new FiltrosDTO(null, null, "MUSICA", null, null, null, null, 0);
        ResultadoBusquedaEvento resultado = eventoService.buscarEventos(filtros);

        assertEquals(1, resultado.totalElementos());
        assertEquals("MUSICA", resultado.eventos().get(0).categoria().getTipo());
    }

    @Test
    public void buscarEventos_filtraPorRangoDeFechas() {

        FiltrosDTO filtros = new FiltrosDTO(LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 30), null, null, null, null, null, 0);
        ResultadoBusquedaEvento resultado = eventoService.buscarEventos(filtros);

        assertEquals(1, resultado.totalElementos());
        assertEquals("E2", resultado.eventos().get(0).id());
    }

    @Test
    public void buscarEventos_filtraPorUbicacion() {

        FiltrosDTO filtros = new FiltrosDTO(null, null, null, "La Plata", null, null, null, 0);
        ResultadoBusquedaEvento resultado = eventoService.buscarEventos(filtros);

        assertEquals(1, resultado.totalElementos());
        assertEquals("La Plata", resultado.eventos().get(0).ubicacion().localidad());
    }

    @Test
    public void buscarEventos_filtraPorPrecio() {

        FiltrosDTO filtros = new FiltrosDTO(null, null, null, null, 600.0, null, null, 0);
        ResultadoBusquedaEvento resultado = eventoService.buscarEventos(filtros);

        assertEquals(1, resultado.totalElementos());
        assertEquals("E2", resultado.eventos().get(0).id());
    }

    @Test
    public void buscarEventos_filtraPorPalabrasClave() {

        FiltrosDTO filtros = new FiltrosDTO(null, null, null, null, null, null, "rock vivo", 0);
        ResultadoBusquedaEvento resultado = eventoService.buscarEventos(filtros);

        assertEquals(1, resultado.totalElementos());
        assertEquals("E1", resultado.eventos().get(0).id());
    }

    @Test
    public void buscarEventos_combinacionDeFiltros() {

        FiltrosDTO filtros = new FiltrosDTO(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 9, 30), "MUSICA", "La Plata", 2000.0, 500.0, "rock", 0);
        ResultadoBusquedaEvento resultado = eventoService.buscarEventos(filtros);

        assertEquals(1, resultado.totalElementos());
        assertEquals("E1", resultado.eventos().get(0).id());
    }

    @Test
    public void cerrarEventosProximos_cierraEventosEn24Horas() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaProxima = ahora.plusHours(23);

        Evento eventoProximo = new Evento(
                "E3",
                "Evento próximo",
                "Descripción",
                fechaProxima,
                fechaProxima.toLocalTime().toString(),
                2.0f,
                new Ubicacion("", "", "CABA", ""),
                50,
                10,
                new Precio("ARS", 1000f),
                organizadorMock,
                new EstadoEvento("4", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                new Categoria("TEST"),
                new ArrayList<>(),
                null
        );

        Evento eventoLejano = new Evento(
                "E4",
                "Evento lejano",
                "Descripción",
                ahora.plusDays(2),
                "20:00",
                2.0f,
                new Ubicacion("", "", "CABA", ""),
                50,
                10,
                new Precio("ARS", 1000f),
                organizadorMock,
                new EstadoEvento("5", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                new Categoria("TEST"),
                new ArrayList<>(),
                null
        );

        List<Evento> eventos = Arrays.asList(eventoProximo, eventoLejano);
        when(eventoRepository.getEventos()).thenReturn(eventos);
        when(eventoRepository.getEvento("E3")).thenReturn(Optional.of(eventoProximo));
        when(generadorIDService.generarID()).thenReturn("nuevo-estado-id");

        eventoService.cerrarEventosProximos();

        verify(eventoRepository).actualizarEvento(argThat(evento ->
                evento.id().equals("E3") &&
                        evento.estado().getTipoEstado().equals(TipoEstadoEvento.NO_ACEPTA_INSCRIPCIONES)
        ));
        verify(eventoRepository, never()).actualizarEvento(argThat(evento ->
                evento.id().equals("E4")
        ));
    }

    @Test
    public void cerrarEventosProximos_noAfestaEventosYaCerrados() {
        LocalDateTime fechaProxima = LocalDateTime.now().plusHours(12);

        Evento eventoCerrado = new Evento(
                "E5",
                "Evento ya cerrado",
                "Descripción",
                fechaProxima,
                fechaProxima.toLocalTime().toString(),
                2.0f,
                new Ubicacion("", "", "CABA", ""),
                50,
                10,
                new Precio("ARS", 1000f),
                organizadorMock,
                new EstadoEvento("6", TipoEstadoEvento.NO_ACEPTA_INSCRIPCIONES, LocalDateTime.now()),
                new Categoria("TEST"),
                new ArrayList<>(),
                null
        );

        when(eventoRepository.getEventos()).thenReturn(Arrays.asList(eventoCerrado));

        eventoService.cerrarEventosProximos();

        verify(eventoRepository, never()).actualizarEvento(any());
    }

    @Test
    public void cerrarEventosProximos_cierraEventosMismaFechaHoraProxima() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaHoy = LocalDateTime.of(ahora.toLocalDate(), ahora.toLocalTime().plusHours(12));

        Evento eventoHoy = new Evento(
                "E6",
                "Evento hoy",
                "Descripción",
                fechaHoy,
                fechaHoy.toLocalTime().toString(),
                2.0f,
                new Ubicacion("", "", "CABA", ""),
                50,
                10,
                new Precio("ARS", 1000f),
                organizadorMock,
                new EstadoEvento("7", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                new Categoria("TEST"),
                new ArrayList<>(),
                null
        );

        when(eventoRepository.getEventos()).thenReturn(Arrays.asList(eventoHoy));
        when(eventoRepository.getEvento("E6")).thenReturn(Optional.of(eventoHoy));
        when(generadorIDService.generarID()).thenReturn("nuevo-estado-id");

        eventoService.cerrarEventosProximos();

        verify(eventoRepository).actualizarEvento(argThat(evento ->
                evento.id().equals("E6") &&
                        evento.estado().getTipoEstado().equals(TipoEstadoEvento.NO_ACEPTA_INSCRIPCIONES)
        ));
    }
}
