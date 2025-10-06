package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.location.Provincia;

import java.util.Optional;

@Repository
public interface ProvinciaRepositoryDB extends MongoRepository<Provincia, String> {
    Optional<Provincia> findByNombreIgnoreCase(String nombre);
}
