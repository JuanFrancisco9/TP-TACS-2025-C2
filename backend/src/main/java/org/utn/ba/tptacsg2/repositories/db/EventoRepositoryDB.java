package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.events.Evento;

public interface EventoRepositoryDB extends MongoRepository<Evento, String> {}