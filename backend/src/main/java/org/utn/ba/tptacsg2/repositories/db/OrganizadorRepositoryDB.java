package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Organizador;

import java.util.Optional;

@Repository
public interface OrganizadorRepositoryDB extends MongoRepository<Organizador, String> {

    Optional<Organizador> findOneByUsuario_Id(String usuarioId);

    boolean existsByUsuario_Id(String usuarioId);

}
