package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;

import java.util.ArrayList;
import java.util.List;

@Service
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
