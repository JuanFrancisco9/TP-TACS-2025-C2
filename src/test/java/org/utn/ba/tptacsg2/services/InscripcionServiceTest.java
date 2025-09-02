package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.utn.ba.tptacsg2.dtos.SolicitudInscripcion;
import org.utn.ba.tptacsg2.dtos.output.Waitlist;
import org.utn.ba.tptacsg2.exceptions.InscripcionNoEncontradaException;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.*;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EstadoInscripcionRepository;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InscripcionServiceTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private InscripcionRepository inscripcionRepository;
    @Mock private EstadoInscripcionRepository estadoInscripcionRepository;
    @Mock private WaitlistService waitlistService;
    @Mock private GeneradorIDService generadorIDService;
    @Mock private EventoService eventoService;
    @Mock private EventoLockService eventoLockService;

    @InjectMocks
    private InscripcionService inscripcionService;

    private Participante participante;
    private Evento evento;
    private static String ID_EVENTO_VALIDO = "1";

    @BeforeEach
    public void setUp() {
        participante = new Participante("1", "Pepito", "Pépez", "123456789");
        evento = new Evento(ID_EVENTO_VALIDO, "Evento mock", "", LocalDateTime.now(), "1900", 5F, new Ubicacion("","","", ""), 3, new Precio("ARS", 10F), new Organizador("1","","",""), new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), null);
        lenient().when(eventoRepository.getEvento(ID_EVENTO_VALIDO)).thenReturn(Optional.of(evento));
        lenient().when(eventoLockService.getLock(ID_EVENTO_VALIDO)).thenReturn(new ReentrantLock());
        lenient().when(eventoRepository.getEvento(ID_EVENTO_VALIDO)).thenReturn(Optional.of(evento));

    }

    @Test
    public void inscripcionSaleBien() {

        when(eventoService.cuposDisponibles(evento)).thenReturn(1);

        SolicitudInscripcion solicitudInscripcion = new SolicitudInscripcion(participante, ID_EVENTO_VALIDO);
        Inscripcion inscripcion = inscripcionService.inscribir(solicitudInscripcion);

        assertEquals(inscripcion.estado().getTipoEstado(), TipoEstadoInscripcion.ACEPTADA);
        assertEquals(inscripcion.evento(),evento);
        assertEquals(inscripcion.participante(),participante);
    }

    @Test
    public void inscripcionAWaitlist_cuandoNoHayMasLugar() {
        when(eventoService.cuposDisponibles(evento)).thenReturn(0);
        SolicitudInscripcion solicitudInscripcion = new SolicitudInscripcion(participante, ID_EVENTO_VALIDO);

        Inscripcion inscripcion = inscripcionService.inscribir(solicitudInscripcion);
        verify(waitlistService).inscribirAWaitlist(solicitudInscripcion);
    }

    @Test
    void cancelarInscripcion_deberiaCancelarYLiberarCupoDeWaitlist() {
        // Arrange
        String inscripcionId = "insc-1";
        String eventoId = "evt-1";
        Evento evento = mock(Evento.class);

        EstadoInscripcion estadoActual = new EstadoInscripcion("estado-1", TipoEstadoInscripcion.ACEPTADA, LocalDateTime.now());
        Inscripcion inscripcion = new Inscripcion(inscripcionId, participante, LocalDateTime.now(), estadoActual, evento);

        when(inscripcionRepository.getInscripcionById(inscripcionId)).thenReturn(Optional.of(inscripcion));
        when(generadorIDService.generarID()).thenReturn("nuevo-estado-id");

        // Simular que hay alguien en la waitlist
        Inscripcion inscripcionWaitlist = new Inscripcion("waitlist-1", participante, LocalDateTime.now(),
                new EstadoInscripcion("estado-wait", TipoEstadoInscripcion.PENDIENTE, LocalDateTime.now()), evento);
        when(inscripcionRepository.getPrimerInscripcionDeWaitlist(evento)).thenReturn(inscripcionWaitlist);

        // Act
        Inscripcion resultado = inscripcionService.cancelarInscripcion(inscripcionId);

        // Assert
        assertEquals(inscripcionId, resultado.id());
        assertEquals(TipoEstadoInscripcion.CANCELADA, resultado.estado().getTipoEstado());

        // Verifica que se haya actualizado el estado de la inscripción en waitlist a ACEPTADA
        ArgumentCaptor<Inscripcion> inscripcionActualizadaCaptor = ArgumentCaptor.forClass(Inscripcion.class);
        verify(inscripcionRepository).actualizarInscripcion(inscripcionActualizadaCaptor.capture());
        Inscripcion actualizada = inscripcionActualizadaCaptor.getValue();
        assertEquals("waitlist-1", actualizada.id());
        assertEquals(TipoEstadoInscripcion.ACEPTADA, actualizada.estado().getTipoEstado());

        verify(estadoInscripcionRepository, times(2)).guardarEstadoInscripcion(any(EstadoInscripcion.class));
    }

    @Test
    void cancelarInscripcion_lanzaExcepcionSiNoExiste() {
        when(inscripcionRepository.getInscripcionById("no-existe")).thenReturn(Optional.empty());
        assertThrows(InscripcionNoEncontradaException.class, () -> inscripcionService.cancelarInscripcion("no-existe"));
    }

    @Test
    void getWaitlist_eventoValido_devuelveWaitlist() {
        // Arrange
        Evento eventoValido = this.evento;
        String eventoId = ID_EVENTO_VALIDO;

        Inscripcion insc1 = new Inscripcion("w1", participante, LocalDateTime.now(),
                new EstadoInscripcion("e1", TipoEstadoInscripcion.PENDIENTE, LocalDateTime.now()), eventoValido);
        Inscripcion insc2 = new Inscripcion("w2", participante, LocalDateTime.now(),
                new EstadoInscripcion("e2", TipoEstadoInscripcion.PENDIENTE, LocalDateTime.now()), eventoValido);

        when(inscripcionRepository.getWailist(eventoValido)).thenReturn(java.util.List.of(insc1, insc2));

        // Act
        Waitlist waitlist = inscripcionService.getWaitlist(eventoId);

        // Assert
        assertNotNull(waitlist);
        assertEquals(eventoValido, waitlist.evento());
        assertEquals(2, waitlist.inscripcionesSinConfirmar().size());
        assertTrue(waitlist.inscripcionesSinConfirmar().contains(insc1));
        assertTrue(waitlist.inscripcionesSinConfirmar().contains(insc2));
    }
}
