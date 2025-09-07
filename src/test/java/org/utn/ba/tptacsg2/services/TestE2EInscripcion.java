package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.utn.ba.tptacsg2.dtos.SolicitudInscripcion;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.events.Ubicacion;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;
import org.utn.ba.tptacsg2.services.InscripcionService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestE2EInscripcion {

    @Autowired
    InscripcionService inscripcionService;
    @Autowired
    EventoRepository eventoRepository;
    @Autowired
    InscripcionRepository inscripcionRepository;


    @BeforeEach
    public void setUp() {

    }

    @Test
    void promueveWaitlist_alCancelarPrimeraInscripcion() {
        // 1) evento CONFIRMADO con cupo=1
        Evento evento = eventoConfirmado("EVT-1", 1);
        eventoRepository.guardarEvento(evento);

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
        Inscripcion bReload = inscripcionRepository.getInscripcionById(b.id()).orElseThrow();
        assertEquals(TipoEstadoInscripcion.ACEPTADA, bReload.estado().getTipoEstado(), "B debería haberse promovido a ACEPTADA");

        // 6) la waitlist del evento debe quedar vacía
        assertTrue(inscripcionRepository.getWailist(evento).isEmpty(), "La waitlist debería quedar vacía");
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
                null,
                null,
                new Organizador("ORG1", "Org", "Uno", "30111222", null),
                new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                null,
                null
        );
    }


}

