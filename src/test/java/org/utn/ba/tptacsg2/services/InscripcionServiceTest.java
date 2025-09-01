package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.*;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.dtos.SolicitudInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EstadoInscripcionRepository;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InscripcionServiceTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private InscripcionRepository inscripcionRepository;
    @Mock private EstadoInscripcionRepository estadoInscripcionRepository;
    @Mock private WaitlistService waitlistService;
    @Mock private GeneradorIDService generadorIDService;
    @Mock private EventoService eventoService;

    @InjectMocks
    private InscripcionService inscripcionService;

    private Participante participante;
    private Evento evento;
    private static String ID_EVENTO_VALIDO = "1";

    @BeforeEach
    public void setUp() {
        participante = new Participante("1", "Pepito", "Pépez", "123456789");
        evento = new Evento(ID_EVENTO_VALIDO, "Evento mock", "", LocalDateTime.now(), "1900", 5F, new Ubicacion("","",""), 3,0, new Precio("ARS", 10F), new Organizador("1","","",""), new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), null, new ArrayList<>());
        when(eventoRepository.getEvento(ID_EVENTO_VALIDO)).thenReturn(Optional.of(evento));

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
}
