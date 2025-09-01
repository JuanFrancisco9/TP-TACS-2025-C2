package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcionV2;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EstadoInscripcionRepository {
    private final List<EstadoInscripcionV2> estadosDeInscripcion = new ArrayList<>();

    public void guardarEstadoInscripcion(EstadoInscripcionV2 estadoInscripcion) {
        this.estadosDeInscripcion.add(estadoInscripcion);
    }

    public EstadoInscripcionV2 getEstadoInscripcionById(String id) {
        return this.estadosDeInscripcion.stream()
                .filter(estado -> estado.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @PostConstruct
    public void init() {
       guardarEstadoInscripcion(new EstadoInscripcionV2("1", TipoEstadoInscripcion.ACEPTADA, null, LocalDateTime.now()));
       guardarEstadoInscripcion(new EstadoInscripcionV2("2", TipoEstadoInscripcion.PENDIENTE, null, LocalDateTime.now()));
    }
}
