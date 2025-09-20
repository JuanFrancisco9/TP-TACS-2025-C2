package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.models.users.Usuario;

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

    public Optional<Organizador> getOrganizadorPorUsuarioId(String idUsuario) {
        return this.organizadores.stream()
                .filter(o -> o.usuario() != null && o.usuario().id().equals(idUsuario))
                .findFirst();
    }

    @PostConstruct
    public void initializeData() {
        this.guardarOrganizadro(new Organizador("1", "Juan", "Pérez", "12345678", new Usuario("organizador1", "organizado1","password1",Rol.ROLE_ORGANIZER )));
        this.guardarOrganizadro(new Organizador("2", "María", "González", "87654321",new Usuario("organizador2", "organizador2","password2", Rol.ROLE_ORGANIZER)));
    }
}
