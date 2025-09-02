package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.utn.ba.tptacsg2.dtos.SolicitudInscripcion;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.*;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InscripcionServiceConcurrentTest {

    @Autowired
    InscripcionService inscripcionService;
    @Autowired
    EventoRepository eventoRepository;
    @Autowired
    InscripcionRepository inscripcionRepository;

    private Evento eventoCon1Cupo;
    private final String ID_EVENTO_1_CUPO = "EVENTO_1_CUPO";

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
                new Organizador("ORG1", "Org", "Uno", "30111222"),
                new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                new Categoria("Test"),
                new ArrayList<>()
        );
    }

    @RepeatedTest(10)
    void sobreInscripcion_oErroresConcurrencia_conCupoUno() throws Exception {

        eventoRepository.guardarEvento(eventoCon1Cupo);

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
                        new Participante("U" + i, "Nombre" + i, "Apellido" + i, "DNI" + i),
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


        var aceptadas = inscripcionRepository.getInscripcionesAEvento(eventoCon1Cupo).stream()
                .filter(i -> i.estado().getTipoEstado() == TipoEstadoInscripcion.ACEPTADA)
                .count();
        var enWaitlist = inscripcionRepository.getInscripcionesAEvento(eventoCon1Cupo).stream()
                .filter(i -> i.estado().getTipoEstado() == TipoEstadoInscripcion.PENDIENTE)
                .count();

        // Solo puede haber una inscripcion aceptada, el resto tiene que estar pendiente
        assertTrue(errors.isEmpty(), "Aparecieron errores por concurrencia: " + errors);
        assertEquals(1, aceptadas,
                "Nunca debería haber más aceptadas que el cupo; aceptadas=" + aceptadas);
        assertEquals(threads - 1, enWaitlist);
    }

}

