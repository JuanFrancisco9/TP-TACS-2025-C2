package org.utn.ba.tptacsg2.repositories;

import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.models.users.Rol;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {
    private final List<Usuario> usuarios = new ArrayList<>();

    public void save(Usuario usuario) {
        usuarios.add(usuario);
    }

    public List<Usuario> findAll() {
        return usuarios;
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarios.stream().filter(u -> u.username().equals(username)).findFirst();
    }
    @PostConstruct
    public void initializeData() {
        save(new Usuario("1", "admin", "$argon2id$v=19$m=65536,t=4,p=1$Y6QXibp2pRk+u6XDSSX6Wg$vaFyKiCj6Tvl06OGHuJtPaw5+4iZDi4f2iN0jrsYYLs", Rol.ROLE_ADMIN));
        save(new Usuario("2", "usuario", "$argon2id$v=19$m=65536,t=4,p=1$hC1J7qKqgmkSUfl8kMdQow$wva2eKpy3Mw8/oJPvJw5JdPse+cEJ73EdmcT6uhcXmU", Rol.ROLE_USER));
        save(new Usuario("3", "organizador", "$argon2id$v=19$m=65536,t=4,p=1$IDXLIGuWc88CLL+7VyhCOA$CXr5e1xeozTTolyjDn1PNX1cs9uHqXFbH6TrtDKOCtk", Rol.ROLE_ORGANIZER));
    }
}
