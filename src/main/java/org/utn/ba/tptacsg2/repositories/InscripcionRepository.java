package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class InscripcionRepository {
    private final Evento evento = new Evento("1", "Evento Test", "Evento de testeo", null, null, null, null, 100, null,
                                       new Organizador("1", "o", "1", "111"), new EstadoEvento(TipoEstadoEvento.CONFIRMADO, null) );
    private final Participante participante1 = new Participante("2", "p", "2", "333");
    private final Participante participante2 = new Participante("3", "p", "3", "444");

    private final List<Inscripcion> inscripciones =  new ArrayList<>(
            Arrays.asList(
                    new Inscripcion("1", participante1, null, new EstadoInscripcion(TipoEstadoInscripcion.ACEPTADA, null), evento),
                    new Inscripcion("2", participante2, null, new EstadoInscripcion(TipoEstadoInscripcion.PENDIENTE, null), evento)
            )
    );

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }
    public void guardarInscripcion(Inscripcion inscripcion) {
        inscripciones.add(inscripcion);
    }
    public List<Inscripcion> getInscripcionesAEvento(Evento evento) {
        return this.getInscripciones().stream()
                .filter(inscripcion -> inscripcion.evento().id().equals(evento.id()))
                .toList();
    }

    public List<Inscripcion> getInscripcionesDeParticipante(String idParticipante) {
        return this.getInscripciones().stream()
                .filter(inscripcion -> inscripcion.participante() != null &&
                        inscripcion.participante().id().equals(idParticipante))
                .toList();
    }
}
