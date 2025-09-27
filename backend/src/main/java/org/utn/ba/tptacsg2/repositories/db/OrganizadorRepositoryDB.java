package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.actors.Organizador;

public interface OrganizadorRepositoryDB extends MongoRepository<Organizador, String> {}
