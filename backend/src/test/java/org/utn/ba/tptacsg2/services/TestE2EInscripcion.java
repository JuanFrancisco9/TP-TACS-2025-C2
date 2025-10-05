package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.utn.ba.tptacsg2.dtos.SolicitudInscripcion;
import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Categoria;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.Precio;
import org.utn.ba.tptacsg2.models.events.Ubicacion;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.repositories.db.EstadoInscripcionRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.EventoRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.InscripcionRepositoryDB;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestE2EInscripcion {

    @InjectMocks
    private InscripcionService inscripcionService;

    @Mock
    private EventoRepositoryDB eventoRepository;
    @Mock
    private InscripcionRepositoryDB inscripcionRepository;
    @Mock
    private WaitlistService waitlistService;
    @Mock
    private GeneradorIDService generadorIDService;
    @Mock
    private EventoService eventoService;
    @Mock
    private EstadoInscripcionRepositoryDB estadoInscripcionRepository;
    @Mock
    private EventoLockService eventoLockService;

    private ConcurrentMap<String, Evento> eventos;
    private ConcurrentMap<String, Inscripcion> inscripciones;
    private AtomicInteger sequence;
    private Map<String, ReentrantLock> locks;

    @BeforeEach
    public void setUp() {
        eventos = new ConcurrentHashMap<>();
        inscripciones = new ConcurrentHashMap<>();
        sequence = new AtomicInteger(1);
        locks = new ConcurrentHashMap<>();

        when(generadorIDService.generarID()).thenAnswer(invocation -> "ID-" + sequence.getAndIncrement());
        when(estadoInscripcionRepository.save(any(EstadoInscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(eventoRepository.save(any(Evento.class))).thenAnswer(invocation -> {
            Evento evento = invocation.getArgument(0);
            eventos.put(evento.id(), evento);
            return evento;
        });

        when(eventoRepository.findById(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(eventos.get(invocation.getArgument(0))));

        when(eventoLockService.getLock(anyString()))
                .thenAnswer(invocation -> locks.computeIfAbsent(invocation.getArgument(0), key -> new ReentrantLock()));

        when(eventoService.cuposDisponibles(any(Evento.class))).thenAnswer(invocation -> {
            Evento evento = invocation.getArgument(0);
            long aceptadas = inscripciones.values().stream()
                    .filter(inscripcion -> inscripcion.evento() != null
                            && evento.id().equals(inscripcion.evento().id())
                            && inscripcion.estado() != null
                            && inscripcion.estado().getTipoEstado() == TipoEstadoInscripcion.ACEPTADA)
                    .count();
            return Math.toIntExact(evento.cupoMaximo() - aceptadas);
        });

        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(invocation -> {
            Inscripcion inscripcion = invocation.getArgument(0);
            inscripciones.put(inscripcion.id(), inscripcion);
            return inscripcion;
        });

        when(inscripcionRepository.findById(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(inscripciones.get(invocation.getArgument(0))));

        when(inscripcionRepository.findFirstInWaitlistByEventoAndTipoEstado(anyString(), eq(TipoEstadoInscripcion.PENDIENTE)))
                .thenAnswer(invocation -> {
                    String eventoId = invocation.getArgument(0);
                    return inscripciones.values().stream()
                            .filter(inscripcion -> inscripcion.evento() != null
                                    && eventoId.equals(inscripcion.evento().id())
                                    && inscripcion.estado() != null
                                    && inscripcion.estado().getTipoEstado() == TipoEstadoInscripcion.PENDIENTE)
                            .sorted(Comparator.comparing(Inscripcion::fechaRegistro))
                            .findFirst();
                });

        when(inscripcionRepository.findWaitlistByEventoOrderByFechaAsc(anyString(), eq(TipoEstadoInscripcion.PENDIENTE)))
                .thenAnswer(invocation -> {
                    String eventoId = invocation.getArgument(0);
                    return inscripciones.values().stream()
                            .filter(inscripcion -> inscripcion.evento() != null
                                    && eventoId.equals(inscripcion.evento().id())
                                    && inscripcion.estado() != null
                                    && inscripcion.estado().getTipoEstado() == TipoEstadoInscripcion.PENDIENTE)
                            .sorted(Comparator.comparing(Inscripcion::fechaRegistro))
                            .toList();
                });

        when(waitlistService.inscribirAWaitlist(any(SolicitudInscripcion.class))).thenAnswer(invocation -> {
            SolicitudInscripcion solicitud = invocation.getArgument(0);
            Evento evento = eventos.get(solicitud.evento_id());
            EstadoInscripcion estadoPendiente = new EstadoInscripcion("EST-P-" + sequence.getAndIncrement(), TipoEstadoInscripcion.PENDIENTE, LocalDateTime.now());
            Inscripcion inscripcionPendiente = new Inscripcion(
                    "INS-P-" + sequence.getAndIncrement(),
                    solicitud.participante(),
                    LocalDateTime.now(),
                    estadoPendiente,
                    evento
            );
            estadoPendiente.setInscripcion(inscripcionPendiente);
            return inscripcionPendiente;
        });
    }

    @Test
    void promueveWaitlist_alCancelarPrimeraInscripcion() {
        // 1) evento CONFIRMADO con cupo=1
        Evento evento = eventoConfirmado("EVT-1", 1);
        eventoRepository.save(evento);

        // 2) se inscribe A -> ACEPTADA
        Inscripcion a = inscripcionService.inscribir(new SolicitudInscripcion(
                participante("U1"),
                evento.id()
        ));
        assertEquals(TipoEstadoInscripcion.ACEPTADA, a.estado().getTipoEstado(), "A debería quedar ACEPTADA");

        // 3) se inscribe B -> PENDIENTE (waitlist)
        Inscripcion b = inscripcionService.inscribir(new SolicitudInscripcion(
                participante("U2"),
                evento.id()
        ));
        assertEquals(TipoEstadoInscripcion.PENDIENTE, b.estado().getTipoEstado(), "B debería quedar en WAITLIST");

        // 4) cancelar A
        inscripcionService.cancelarInscripcion(a.id());

        // 5) B debe haberse promovido a ACEPTADA
        Inscripcion bReload = inscripcionRepository.findById(b.id()).orElseThrow();
        assertEquals(TipoEstadoInscripcion.ACEPTADA, bReload.estado().getTipoEstado(), "B debería haberse promovido a ACEPTADA");

        // 6) la waitlist del evento debe quedar vacía
        assertTrue(inscripcionRepository.findWaitlistByEventoOrderByFechaAsc(evento.id(), TipoEstadoInscripcion.PENDIENTE).isEmpty(), "La waitlist debería quedar vacía");
    }


    private Participante participante(String id) {
        return new Participante(
                id,
                "Nombre " + id,
                "Apellido " + id,
                "DNI-" + id,
                new Usuario(null,null,null, null)
        );
    }

    private Evento eventoConfirmado(String id, int cupoMaximo) {
        return new Evento(
                id,
                "Titulo " + id,
                "Descripcion " + id,
                LocalDateTime.now().plusDays(7),
                "10:00",
                2.0f,
                new Ubicacion("-34.6", "-58.4", "CABA", ""),
                cupoMaximo,
                0,
                new Precio("ARS", 1000F),
                new Organizador("ORG1", "Org", "Uno", "30111222", null),
                new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                new Categoria("Test"),
                List.of(),
                null
        );
    }


}
