package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.users.Usuario;

import java.util.Optional;

public interface OrganizadorRepositoryDB extends MongoRepository<Organizador, String> {
    Optional<Organizador> findByUsuario(Usuario usuario);
}
