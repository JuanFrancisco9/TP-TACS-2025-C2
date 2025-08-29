package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.*;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    private final UsuarioRepository repo;

    public UserDetailsServiceImp(UsuarioRepository repo) {
        this.repo = repo;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No existe: " + username));

        // Ajustá esta conversión según cómo guardes roles/authorities
        String authorities = String.valueOf(u.rol());

        return User.withUsername(u.username())
                .password(u.passwordHash())
                .authorities(authorities)
                .build();
    }
}
