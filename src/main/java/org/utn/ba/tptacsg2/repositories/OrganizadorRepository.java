package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.models.actors.Organizador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class OrganizadorRepository {
    private final List<Organizador> organizadores = new ArrayList<>(
            List.of(
                    new Organizador("1", "o", "1", "111")
            )
    );

    public List<Organizador> getOrganizadores() {
        return organizadores;
    }

    public void guardarOrganizadro(Organizador organizador) {
        organizadores.add(organizador);
    }

    public Optional<Organizador> getOrganizador(String id) {
        return this.organizadores.stream()
                .filter(o -> o.id().equals(id))
                .findFirst();
    }
}
