package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;

import java.util.ArrayList;
import java.util.Comparator;
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

    public List<Inscripcion> getWailist(Evento evento) {
        return this.getInscripciones().stream().filter(i -> i.evento().id().equals(evento.id()) && i.estado().getTipoEstado() == TipoEstadoInscripcion.PENDIENTE).toList();
    }

    public Inscripcion getPrimerInscripcionDeWaitlist (Evento evento) {

        List<Inscripcion> inscripcionesEnWaitlist = this.getWailist(evento);

        return inscripcionesEnWaitlist.stream().min(Comparator.comparing(Inscripcion::fechaRegistro)).orElse(null);

    }
    public void actualizarInscripcion(Inscripcion inscripcion) {

        Inscripcion inscripcionEnBD = this.inscripciones.stream()
                .filter(i -> i.id().equals(inscripcion.id()))
                .findFirst()
                .orElse(null);

        this.inscripciones.remove(inscripcionEnBD);
        this.inscripciones.add(inscripcion);

    }
}
