package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcionV2;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EstadoInscripcionRepository {
    private final List<EstadoInscripcionV2> estadosDeInscripcion = new ArrayList<>();

    public void guardarEstadoInscripcion(EstadoInscripcionV2 estadoInscripcion) {
        this.estadosDeInscripcion.add(estadoInscripcion);
    }
}
