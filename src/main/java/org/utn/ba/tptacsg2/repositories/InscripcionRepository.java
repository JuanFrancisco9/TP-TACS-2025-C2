package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InscripcionRepository {
    private final List<Inscripcion> inscripciones =  new ArrayList<>();

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
}
