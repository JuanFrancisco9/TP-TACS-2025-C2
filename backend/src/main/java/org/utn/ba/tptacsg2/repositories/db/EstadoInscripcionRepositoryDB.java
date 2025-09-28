package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;


public interface EstadoInscripcionRepositoryDB extends MongoRepository<EstadoInscripcion, String> {}