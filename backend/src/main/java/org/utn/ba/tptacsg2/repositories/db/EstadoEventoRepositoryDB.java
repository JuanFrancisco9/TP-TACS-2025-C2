package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;

@Repository
public interface EstadoEventoRepositoryDB extends MongoRepository<EstadoEvento, String> {}