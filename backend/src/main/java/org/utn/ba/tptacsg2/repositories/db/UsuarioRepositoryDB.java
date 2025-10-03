package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.users.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryDB extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByUsername(String username);
}
