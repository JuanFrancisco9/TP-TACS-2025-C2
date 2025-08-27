package org.utn.ba.tptacsg2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.events.*;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.OrganizadorRepository;
import org.utn.ba.tptacsg2.services.EventoService;
import org.utn.ba.tptacsg2.services.GeneradorIDService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventoServiceTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private OrganizadorRepository organizadorRepository;
    @Mock private GeneradorIDService generadorIDService;

    @InjectMocks
    private EventoService eventoService;

    private Organizador organizadorMock;
    private Evento eventoSinId;
    private SolicitudEvento solicitudEvento;
    private static String idOrganizadorMock;

    @BeforeEach
    public void setUp() {
        idOrganizadorMock="ORG-123";
        organizadorMock = new Organizador(idOrganizadorMock, "Juan", "Perez","78414456");

        eventoSinId = new Evento(
                null,
                "Fiesta UTN",
                "Evento de prueba",
                LocalDateTime.of(2025, 9, 10, 20, 0),
                "20:00",
                3.5f,
                new Ubicacion("-34.6037", "-58.3816", "Av. Medrano 951, CABA"),
                100,
                new Precio("ARS", 5000f),
                null,
                new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.of(2025, 9, 1, 12, 0))
        );

        solicitudEvento = new SolicitudEvento(idOrganizadorMock, eventoSinId);

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

        SolicitudEvento solicitudInvalida = new SolicitudEvento("ORG-INEXISTENTE", eventoSinId);

        assertThrows(RuntimeException.class, () -> {
            eventoService.registrarEvento(solicitudInvalida);
        });

        verify(eventoRepository, org.mockito.Mockito.never()).guardarEvento(org.mockito.Mockito.any());
    }
}
