package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.users.Usuario;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioRepository {
    private List<Usuario> usuarios = new ArrayList<>();

    public void save(Usuario usuario) {
        usuarios.add(usuario);
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
