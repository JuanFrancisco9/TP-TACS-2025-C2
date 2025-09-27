package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;

public interface EstadoEventoRepositoryDB extends MongoRepository<EstadoEvento, String> {}