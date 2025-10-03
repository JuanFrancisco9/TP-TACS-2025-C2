package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.users.Usuario;

import java.util.Optional;

public interface ParticipanteRepositoryDB extends MongoRepository<Participante, String> {
    Optional<Participante> findByUsuario(Usuario user);
}
