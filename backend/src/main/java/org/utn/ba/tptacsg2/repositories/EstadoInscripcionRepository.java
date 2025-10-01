package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.db.EstadoInscripcionRepositoryDB;

import java.time.LocalDateTime;

@Repository
public class EstadoInscripcionRepository {
    private final EstadoInscripcionRepositoryDB repository;

    @Autowired
    public EstadoInscripcionRepository(EstadoInscripcionRepositoryDB repository) {
        this.repository = repository;
    }

    public void guardarEstadoInscripcion(EstadoInscripcion estadoInscripcion) {
        repository.save(estadoInscripcion);
    }

    public EstadoInscripcion getEstadoInscripcionById(String id) {
        return repository.findById(id).orElse(null);
    }
}
