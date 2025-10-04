package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
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
import org.utn.ba.tptacsg2.repositories.db.EstadoInscripcionRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.EventoRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.InscripcionRepositoryDB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceConcurrentTest {

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

    private Evento eventoCon1Cupo;
    private final String ID_EVENTO_1_CUPO = "EVENTO_1_CUPO";
    private ConcurrentMap<String, Inscripcion> inscripciones;
    private AtomicInteger sequence;
    private Map<String, ReentrantLock> locks;

    @BeforeEach
    void setUp() {
        eventoCon1Cupo = new Evento(
                ID_EVENTO_1_CUPO,
                "Mock",
                "Mock",
                LocalDateTime.now(),
                "",
                2.0f,
                new Ubicacion("-34.6", "-58.4", "CABA", ""),
                1,
                0,
                new Precio("Pesos", 100F),
                new Organizador("ORG1", "Org", "Uno", "30111222", null),
                new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                new Categoria("Test"),
                new ArrayList<>(),
                null
        );

        inscripciones = new ConcurrentHashMap<>();
        sequence = new AtomicInteger(1);
        locks = new ConcurrentHashMap<>();

        when(eventoRepository.findById(ID_EVENTO_1_CUPO)).thenReturn(Optional.of(eventoCon1Cupo));
        when(generadorIDService.generarID()).thenAnswer(invocation -> "ID-" + sequence.getAndIncrement());
        when(estadoInscripcionRepository.save(any(EstadoInscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

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

        when(inscripcionRepository.findByEvento_Id(eq(ID_EVENTO_1_CUPO))).thenAnswer(invocation ->
                inscripciones.values().stream()
                        .filter(inscripcion -> inscripcion.evento() != null && ID_EVENTO_1_CUPO.equals(inscripcion.evento().id()))
                        .toList()
        );

        when(waitlistService.inscribirAWaitlist(any(SolicitudInscripcion.class))).thenAnswer(invocation -> {
            SolicitudInscripcion solicitud = invocation.getArgument(0);
            EstadoInscripcion estadoPendiente = new EstadoInscripcion("EST-P-" + sequence.getAndIncrement(), TipoEstadoInscripcion.PENDIENTE, LocalDateTime.now());
            Inscripcion inscripcionPendiente = new Inscripcion(
                    "INS-P-" + sequence.getAndIncrement(),
                    solicitud.participante(),
                    LocalDateTime.now(),
                    estadoPendiente,
                    eventoCon1Cupo
            );
            estadoPendiente.setInscripcion(inscripcionPendiente);
            return inscripcionPendiente;
        });
    }

    @RepeatedTest(10)
    void sobreInscripcion_oErroresConcurrencia_conCupoUno() throws Exception {

        int threads = Math.max(32, Runtime.getRuntime().availableProcessors() * 4);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CyclicBarrier startLine = new CyclicBarrier(threads);
        CountDownLatch done = new CountDownLatch(threads);
        Queue<Throwable> errors = new ConcurrentLinkedQueue<>();

        // Emito varias inscripciones a la vez, y me guardo los errores que puedan haber
        IntStream.range(0, threads).forEach(i -> pool.submit(() -> {
            try {
                startLine.await(10, TimeUnit.SECONDS);
                inscripcionService.inscribir(new SolicitudInscripcion(
                        new Participante("U" + i, "Nombre" + i, "Apellido" + i, "DNI" + i, null),
                        eventoCon1Cupo.id()
                ));
            } catch (Throwable t) {
                errors.add(t);
            } finally {
                done.countDown();
            }
        }));

        done.await(15, TimeUnit.SECONDS);
        pool.shutdownNow();


        long aceptadas = inscripcionRepository.findByEvento_Id(eventoCon1Cupo.id()).stream()
                .filter(i -> i.estado().getTipoEstado() == TipoEstadoInscripcion.ACEPTADA)
                .count();
        long enWaitlist = inscripcionRepository.findByEvento_Id(eventoCon1Cupo.id()).stream()
                .filter(i -> i.estado().getTipoEstado() == TipoEstadoInscripcion.PENDIENTE)
                .count();

        // Solo puede haber una inscripcion aceptada, el resto tiene que estar pendiente
        assertTrue(errors.isEmpty(), "Aparecieron errores por concurrencia: " + errors);
        assertEquals(1, aceptadas,
                "Nunca debería haber más aceptadas que el cupo; aceptadas=" + aceptadas);
        assertEquals(threads - 1, enWaitlist);
    }

}
