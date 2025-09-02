package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Participante;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class ParticipanteRepository {
    private final List<Participante> participantes = new ArrayList<>();

    public List<Participante> getParticipantes() {
        return participantes;
    }

    public void guardarParticipante(Participante participante) {
        participantes.add(participante);
    }

    public Optional<Participante> getParticipante(String id) {
        return this.participantes.stream()
                .filter(p -> p.id().equals(id))
                .findFirst();
    }

    @PostConstruct
    public void initializeData() {
        this.guardarParticipante(new Participante("1", "Carlos", "López", "11111111"));
        this.guardarParticipante(new Participante("2", "Ana", "Martínez", "22222222"));
    }
}
