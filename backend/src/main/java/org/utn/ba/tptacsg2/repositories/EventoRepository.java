package org.utn.ba.tptacsg2.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.events.*;
import org.utn.ba.tptacsg2.repositories.db.EventoRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.OrganizadorRepositoryDB;

import java.util.List;
import java.util.Optional;

@Repository
public class EventoRepository {
    private final EventoRepositoryDB eventoDB;
    private final OrganizadorRepository organizadorRepository;

    @Autowired
    public EventoRepository(EventoRepositoryDB eventoDB,  OrganizadorRepository organizadorRepository) {
        this.eventoDB = eventoDB;
        this.organizadorRepository = organizadorRepository;
    }

    public List<Evento> getEventos() {
        return eventoDB.findAll();
    }

    public void guardarEvento(Evento evento) {
        eventoDB.save(evento);
    }

    public Optional<Evento> getEvento(String id) {
        return eventoDB.findById(id);
    }

    // Busca Eventos a partir de organizador.usuario.id
    public List<Evento> getEventosDeOrganizadorPorUsuario(String idUsuario) {
        Organizador organizador = organizadorRepository.getOrganizadorPorUsuarioId(idUsuario).orElse(null);
        return eventoDB.findByOrganizador(organizador);
    }

    public void actualizarEvento(Evento evento){
        eventoDB.save(evento);
    }

    public List<Evento> getEventosDeOrganizador(String idOrganizador) {
        Organizador organizador = organizadorRepository.getOrganizador(idOrganizador).orElse(null);
        if (organizador == null) {
            return List.of();
        }

        return eventoDB.findByOrganizador(organizador);
    }
}
