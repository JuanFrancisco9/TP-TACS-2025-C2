package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.db.EstadoEventoRepositoryDB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EstadoEventoRepository {
    private EstadoEventoRepositoryDB repository;

    @Autowired
    public EstadoEventoRepository (EstadoEventoRepositoryDB repository) {
        this.repository = repository;
    }

    public void guardarEstadoEvento(EstadoEvento estadoEvento) {
        repository.save(estadoEvento);
    }

    public EstadoEvento getEstadoInscripcionById(String id) {
        return repository.findById(id).orElse(null);
    }
}
