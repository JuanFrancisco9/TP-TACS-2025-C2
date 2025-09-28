package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EstadoInscripcionRepository {
    private final List<EstadoInscripcion> estadosDeInscripcion = new ArrayList<>();

    public void guardarEstadoInscripcion(EstadoInscripcion estadoInscripcion) {
        this.estadosDeInscripcion.add(estadoInscripcion);
    }

    public EstadoInscripcion getEstadoInscripcionById(String id) {
        return this.estadosDeInscripcion.stream()
                .filter(estado -> estado.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @PostConstruct
    public void init() {
       guardarEstadoInscripcion(new EstadoInscripcion("1", TipoEstadoInscripcion.ACEPTADA, null, LocalDateTime.now()));
       guardarEstadoInscripcion(new EstadoInscripcion("2", TipoEstadoInscripcion.PENDIENTE, null, LocalDateTime.now()));
    }
}
