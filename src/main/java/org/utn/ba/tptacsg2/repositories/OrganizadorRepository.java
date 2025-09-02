package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Organizador;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrganizadorRepository {
    private final List<Organizador> organizadores = new ArrayList<>();

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

    @PostConstruct
    public void initializeData() {
        this.guardarOrganizadro(new Organizador("1", "Juan", "Pérez", "12345678", null));
        this.guardarOrganizadro(new Organizador("2", "María", "González", "87654321",null));
    }
}
