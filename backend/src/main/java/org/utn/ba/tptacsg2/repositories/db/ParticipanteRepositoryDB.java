package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.actors.Participante;

public interface ParticipanteRepositoryDB extends MongoRepository<Participante, String> {}
