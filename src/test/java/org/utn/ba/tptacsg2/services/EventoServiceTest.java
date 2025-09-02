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
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.OrganizadorRepository;
import org.utn.ba.tptacsg2.services.EventoService;
import org.utn.ba.tptacsg2.services.GeneradorIDService;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventoServiceTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private OrganizadorRepository organizadorRepository;
    @Mock private GeneradorIDService generadorIDService;

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
        idOrganizadorMock="ORG-123";
        idEventoMock="EV-123";
        organizadorMock = new Organizador(idOrganizadorMock, "Juan", "Perez","78414456");

        eventoSinId = new Evento(
                null,
                "Fiesta UTN",
                "Evento de prueba",
                LocalDateTime.of(2025, 9, 10, 20, 0),
                "20:00",
                3.5f,
                new Ubicacion("-34.6037", "-58.3816", "Av. Medrano 951, CABA",""),
                100,
                0,
                new Precio("ARS", 5000f),
                null,
                new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.of(2025, 9, 1, 12, 0))
        , null, new ArrayList<>());

        solicitudEvento = new SolicitudEvento(idOrganizadorMock,
                "Fiesta UTN",
                "Evento de prueba",
                LocalDateTime.of(2025, 9, 10, 20, 0),
                "20:00",
                3.5f,
                new Ubicacion("-34.6037", "-58.3816", "Av. Medrano 951, CABA",""),
                100,
                0,
                new Precio("ARS", 5000f),
                TipoEstadoEvento.CONFIRMADO,
                null,
                new ArrayList<>());

            Categoria categoria1 = new Categoria("MUSICA");
            Categoria categoria2 = new Categoria("TECNOLOGIA");

        eventoValido1 = new Evento("E1", "Concierto de rock vivo", "Musica", LocalDateTime.of(2025, 9, 10, 20, 0), "20:00", 2f, new Ubicacion("", "", "La Plata", "CABA"), 100, new Precio("ARS", 1000f), organizadorMock, new EstadoEvento("2", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), categoria1);
        eventoValido2= new Evento("E2", "Charla", "Tecnologia", LocalDateTime.of(2025, 10, 10, 18, 0), "18:00", 1.5f, new Ubicacion("", "", "CABA", "CABA"), 50, new Precio("ARS", 500f), organizadorMock, new EstadoEvento("2", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), categoria2);
            eventoValido1 = new Evento("E1", "Concierto de rock vivo", "Musica", LocalDateTime.of(2025, 9, 10, 20, 0), "20:00", 2f, new Ubicacion("", "", "La Plata", "CABA"), 100,0, new Precio("ARS", 1000f), organizadorMock, new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), categoria1,null);
            eventoValido2= new Evento("E2", "Charla", "Tecnologia", LocalDateTime.of(2025, 10, 10, 18, 0), "18:00", 1.5f, new Ubicacion("", "", "CABA", "CABA"), 50,0, new Precio("ARS", 500f), organizadorMock, new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), categoria2,null);

            lenient().when(eventoRepository.getEventos()).thenReturn(Arrays.asList(eventoValido1, eventoValido2));

            ReflectionTestUtils.setField(eventoService, "tamanioPagina", 20);
    }

    @Test
    public void registrarEventoGuardaEnMemoria() {
        when(organizadorRepository.getOrganizador(idOrganizadorMock)).thenReturn(Optional.of(organizadorMock));
        when(generadorIDService.generarID()).thenReturn(idOrganizadorMock);

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
                new Ubicacion("-34.6037", "-58.3816", "Av. Medrano 951, CABA",""),
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
    public void cambiarEstadoEvento(){
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
        assertTrue(resultado.eventos().contains(eventoValido1));
        assertTrue(resultado.eventos().contains(eventoValido2));
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
}
