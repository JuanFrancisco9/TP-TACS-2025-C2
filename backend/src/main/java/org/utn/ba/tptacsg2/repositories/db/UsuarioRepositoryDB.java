package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.users.Usuario;

public interface UsuarioRepositoryDB extends MongoRepository<Usuario, String> {}
