package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.models.users.Rol;
import jakarta.annotation.PostConstruct;
import org.utn.ba.tptacsg2.repositories.db.UsuarioRepositoryDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {
    private final UsuarioRepositoryDB usuarioDB;

    public UsuarioRepository(UsuarioRepositoryDB usuarioDB) {
        this.usuarioDB = usuarioDB;
    }

    public void save(Usuario usuario) {
        usuarioDB.save(usuario);
    }

    public List<Usuario> findAll() {
        return usuarioDB.findAll();
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioDB.findByUsername(username);
    }
}
