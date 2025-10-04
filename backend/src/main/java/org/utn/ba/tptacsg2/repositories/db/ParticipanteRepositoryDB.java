package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Participante;

import java.util.Optional;


@Repository
public interface ParticipanteRepositoryDB extends MongoRepository<Participante, String> {

    Optional<Participante> findOneByUsuario_Id(String usuarioId);
    boolean existsByUsuario_Id(String usuarioId);

}
