package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.repositories.db.OrganizadorRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.UsuarioRepositoryDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrganizadorRepository {
    private final OrganizadorRepositoryDB organizadorDB;
    private final UsuarioRepositoryDB usuarioDB;

    @Autowired
    public OrganizadorRepository(OrganizadorRepositoryDB repository,  UsuarioRepositoryDB usuarioDB) {
        this.organizadorDB = repository;
        this.usuarioDB = usuarioDB;
    }

    public List<Organizador> getOrganizadores() {
        return organizadorDB.findAll();
    }

    public void guardarOrganizadro(Organizador organizador) {
        organizadorDB.save(organizador);
    }

    public Optional<Organizador> getOrganizador(String id) {
        return organizadorDB.findById(id);
    }

    public Optional<Organizador> getOrganizadorPorUsuarioId(String idUsuario) {
        Usuario usuario = usuarioDB.findById(idUsuario).orElse(null);
        if (usuario == null)
            return Optional.empty();

        return organizadorDB.findByUsuario(usuario);
    }

    @PostConstruct
    public void initializeData() {
        this.guardarOrganizadro(new Organizador("1", "Juan", "Pérez", "12345678", new Usuario("organizador1", "organizado1","password1",Rol.ROLE_ORGANIZER )));
        this.guardarOrganizadro(new Organizador("2", "María", "González", "87654321",new Usuario("organizador2", "organizador2","password2", Rol.ROLE_ORGANIZER)));
    }
}