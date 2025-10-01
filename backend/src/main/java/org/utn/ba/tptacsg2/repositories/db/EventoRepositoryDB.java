package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.events.Evento;

import java.util.List;

public interface EventoRepositoryDB extends MongoRepository<Evento, String> {
    List<Evento> findByOrganizador_Usuario_Id(String idUsuario);
    List<Evento> findByOrganizador_Id(String idOrganizador);
}