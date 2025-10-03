package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.repositories.db.ParticipanteRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.UsuarioRepositoryDB;
import org.utn.ba.tptacsg2.services.ParticipanteService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class ParticipanteRepository {
    private final ParticipanteRepositoryDB participanteDB;
    private final UsuarioRepositoryDB usuarioDB;

    public ParticipanteRepository(ParticipanteRepositoryDB participanteDB,  UsuarioRepositoryDB usuarioDB) {
        this.participanteDB = participanteDB;
        this.usuarioDB = usuarioDB;
    }

    public List<Participante> getParticipantes() {
        return participanteDB.findAll();
    }

    public void guardarParticipante(Participante participante) {
        participanteDB.save(participante);
    }

    public Optional<Participante> getParticipante(String id) {
        return participanteDB.findById(id);
    }

    public Optional<Participante> getParticipantePorUsuarioId(String idUsuario) {
        Usuario user = usuarioDB.findById(idUsuario).orElse(null);
        if (user == null)
            return Optional.empty();

        return participanteDB.findByUsuario(user);
    }
}
