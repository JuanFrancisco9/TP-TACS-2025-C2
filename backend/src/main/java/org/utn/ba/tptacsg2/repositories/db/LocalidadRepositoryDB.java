package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.location.Localidad;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalidadRepositoryDB extends MongoRepository<Localidad, String> {
    List<Localidad> findByProvinciaIdOrderByNombreAsc(String provinciaId);
    Optional<Localidad> findByProvinciaIdAndNombreIgnoreCase(String provinciaId, String nombre);
}
