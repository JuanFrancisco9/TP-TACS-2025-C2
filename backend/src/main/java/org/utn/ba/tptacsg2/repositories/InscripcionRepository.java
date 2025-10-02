package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.*;
import org.utn.ba.tptacsg2.models.inscriptions.*;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.db.InscripcionRepositoryDB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class InscripcionRepository {
    private final InscripcionRepositoryDB repository;

    public InscripcionRepository(InscripcionRepositoryDB repository) {
        this.repository = repository;
    }

    public List<Inscripcion> getInscripciones() {
        return repository.findAll();
    }

    public Optional<Inscripcion> getInscripcionById(String id) {
        return repository.findById(id);
    }

    public void guardarInscripcion(Inscripcion inscripcion) {
        repository.save(inscripcion);
    }

    public List<Inscripcion> getInscripcionesAEvento(Evento evento) {
        return repository.findAllByEvento(evento);
    }

    public List<Inscripcion> getWailist(Evento evento) {
        return repository.findAllByEventoAndEstado_TipoEstado(evento, TipoEstadoInscripcion.PENDIENTE);
    }

    public Inscripcion getPrimerInscripcionDeWaitlist (Evento evento) {
        List<Inscripcion> inscripcionesEnWaitlist = this.getWailist(evento);
        return inscripcionesEnWaitlist.stream().min(Comparator.comparing(Inscripcion::fechaRegistro)).orElse(null);
    }

    public void actualizarInscripcion(Inscripcion inscripcion) {
        repository.save(inscripcion);
    }

    public List<Inscripcion> getInscripcionesDeParticipante(String idParticipante) {
        return repository.findAllByParticipante_Id(idParticipante);
    }

    public List<Inscripcion> getAll() {
        return repository.findAll();
    }
}
