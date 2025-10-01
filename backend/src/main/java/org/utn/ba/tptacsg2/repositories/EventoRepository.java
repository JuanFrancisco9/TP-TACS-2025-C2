package org.utn.ba.tptacsg2.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.*;
import org.utn.ba.tptacsg2.repositories.db.EventoRepositoryDB;

import java.util.List;
import java.util.Optional;

@Repository
public class EventoRepository {
    private final EventoRepositoryDB repository;

    @Autowired
    public EventoRepository(EventoRepositoryDB repository) {
        this.repository = repository;
    }

    public List<Evento> getEventos() {
        return repository.findAll();
    }

    public void guardarEvento(Evento evento) {
        repository.save(evento);
    }

    public Optional<Evento> getEvento(String id) {
        return repository.findById(id);
    }

    // Busca Eventos a partir de organizador.usuario.id
    public List<Evento> getEventosDeOrganizadorPorUsuario(String idUsuario) {
        return repository.findByOrganizador_Usuario_Id(idUsuario);
    }

    public void actualizarEvento(Evento evento){
        repository.save(evento);
    }

    public List<Evento> getEventosDeOrganizador(String idOrganizador) {
        return repository.findByOrganizador_Id(idOrganizador);
    }
}
