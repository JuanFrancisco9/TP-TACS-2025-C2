package org.utn.ba.tptacsg2.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.UsuarioDto;
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.repositories.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrar(UsuarioDto usuario) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(usuario.getUsername());
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(usuario.getPassword()));
        nuevoUsuario.setRol(Rol.valueOf(usuario.getRol().strip().toUpperCase()));

        usuarioRepository.save(nuevoUsuario);
    }

    public List<UsuarioDto> getUsuarios() {
        return usuarioRepository.getUsuarios().stream().map(u -> new UsuarioDto(u.getUsername(), u.getPasswordHash(), u.getRol().name())).toList();
    }
}
