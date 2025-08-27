package org.utn.ba.tptacsg2.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param usuario Un objeto UsuarioDto que contiene la información del usuario a registrar.
     * @throws RuntimeException Si el nombre de usuario ya existe en el sistema.
     */
    public void registrar(UsuarioDto usuario) {
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }
        Usuario nuevoUsuario = new Usuario(
                usuario.getId(),
                usuario.getUsername(),
                passwordEncoder.encode(usuario.getPassword()),
                Rol.valueOf(usuario.getRol().strip().toUpperCase())
        );

        usuarioRepository.save(nuevoUsuario);
    }

    public List<UsuarioDto> getUsuarios() {
        return usuarioRepository.finAll().stream().map(u -> new UsuarioDto(u.id(),u.username(), u.passwordHash(), u.rol().name())).toList();
    }

    /**
     * Autentica a un usuario en el sistema.
     *
     * @param usuario Un objeto UsuarioDto que contiene las credenciales del usuario a autenticar.
     * @return El objeto Usuario correspondiente si la autenticación es exitosa.
     * @throws UsernameNotFoundException Si el nombre de usuario no existe en el sistema.
     * @throws RuntimeException Si la contraseña es incorrecta.
     */
    public Usuario login(UsuarioDto usuario) {
        Usuario user = usuarioRepository.finAll().stream()
                .filter(u -> u.username().equals(usuario.getUsername())).findFirst().orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(usuario.getPassword(), user.passwordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        return  user;
    }
}
