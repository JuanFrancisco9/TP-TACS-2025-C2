package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.users.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {
    private final List<Usuario> usuarios = new ArrayList<>();

    public void save(Usuario usuario) {
        usuarios.add(usuario);
    }

    public List<Usuario> finAll() {
        return usuarios;
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarios.stream().filter(u -> u.username().equals(username)).findFirst();
    }
}
