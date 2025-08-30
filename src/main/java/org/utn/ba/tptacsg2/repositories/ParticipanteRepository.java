package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Participante;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class ParticipanteRepository {
    private final List<Participante> participantes = new ArrayList<>(
            Arrays.asList(
                    new Participante("1", "p", "1", "222"),
                    new Participante("2", "p", "2", "333"),
                    new Participante("3", "p", "3", "444")
            )
    );

    public Optional<Participante> getParticipante(String dni){
        return this.participantes.stream().filter(p -> p.dni().equals(dni)).findFirst();
    }

    public List<Participante> getAll(){
        return this.participantes;
    }
}
