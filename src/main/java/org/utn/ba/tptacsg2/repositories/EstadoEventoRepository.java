package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EstadoEventoRepository {
    private final List<EstadoEvento> estadosDeEventos = new ArrayList<>();

    public void guardarEstadoEvento(EstadoEvento estadoEvento) {
        this.estadosDeEventos.add(estadoEvento);
    }

    public EstadoEvento getEstadoInscripcionById(String id) {
        return this.estadosDeEventos.stream()
                .filter(estado -> estado.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @PostConstruct
    public void init() {
        guardarEstadoEvento(new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO,  LocalDateTime.now(), null));
        guardarEstadoEvento(new EstadoEvento("2", TipoEstadoEvento.CONFIRMADO,  LocalDateTime.now(), null));
    }
}
