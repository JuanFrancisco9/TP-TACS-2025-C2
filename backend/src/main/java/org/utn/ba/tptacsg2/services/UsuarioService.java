package org.utn.ba.tptacsg2.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.InputRegistroDto;
import org.utn.ba.tptacsg2.dtos.LoginResponseDto;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.repositories.OrganizadorRepository;
import org.utn.ba.tptacsg2.repositories.ParticipanteRepository;
import org.utn.ba.tptacsg2.repositories.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private ParticipanteRepository participanteRepository;
    private OrganizadorRepository organizadorRepository;
    private GeneradorIDService generadorIDService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          ParticipanteRepository participanteRepository, OrganizadorRepository organizadorRepository,
                          GeneradorIDService generadorIDService) {
        this.participanteRepository = participanteRepository;
        this.organizadorRepository = organizadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.generadorIDService = generadorIDService;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param usuario Un objeto UsuarioDto que contiene la información del usuario a registrar.
     * @throws RuntimeException Si el nombre de usuario ya existe en el sistema.
     */
    public void registrar(InputRegistroDto usuario) {
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }
        Rol rol;
        try {
            rol = Rol.valueOf(usuario.getRol().strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            rol = Rol.ROLE_USER;
        }

        Usuario nuevoUsuario = new Usuario(
                generadorIDService.generarID(),
                usuario.getUsername().strip(),
                passwordEncoder.encode(usuario.getPassword()),
                rol
        );
        switch (rol) {
            case ROLE_ORGANIZER -> crearOrganizador(nuevoUsuario, usuario);
            case ROLE_ADMIN -> { /* no crea perfil adicional */ }
            default -> crearParticipante(nuevoUsuario, usuario);
        }
        usuarioRepository.save(nuevoUsuario);
    }

    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll().stream().map(u -> new Usuario(u.id(),u.username(), u.passwordHash(), u.rol())).toList();
    }

    /**
     * Autentica a un usuario en el sistema.
     *
     * @param usuario Un objeto UsuarioDto que contiene las credenciales del usuario a autenticar.
     * @return El objeto LoginResponseDto con datos del usuario y ID del actor correspondiente si la autenticación es exitosa.
     * @throws UsernameNotFoundException Si el nombre de usuario no existe en el sistema.
     * @throws RuntimeException Si la contraseña es incorrecta.
     */
    public LoginResponseDto login(InputRegistroDto usuario) {
        Usuario user = usuarioRepository.findAll().stream()
                .filter(u -> u.username().equals(usuario.getUsername())).findFirst().orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(usuario.getPassword(), user.passwordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String actorId = null;
        switch (user.rol()) {
            case ROLE_ORGANIZER -> {
                Organizador organizador = organizadorRepository.getOrganizadorPorUsuarioId(user.id()).orElse(null);
                if (organizador != null) {
                    actorId = organizador.id();
                }
            }
            case ROLE_USER -> {
                Participante participante = participanteRepository.getParticipantePorUsuarioId(user.id()).orElse(null);
                if (participante != null) {
                    actorId = participante.id();
                }
            }
        }

        return new LoginResponseDto(user.id(), user.username(), user.rol(), actorId);
    }

    private void crearParticipante(Usuario usuario, InputRegistroDto usuarioDto) {
        Participante participante = new Participante(
                null,
                usuarioDto.getNombre(),
                usuarioDto.getApellido(),
                usuarioDto.getDni(),
                usuario
        );
        participanteRepository.guardarParticipante(participante);
    }

    private void crearOrganizador(Usuario usuario, InputRegistroDto usuarioDto) {
        Organizador organizador = new Organizador(
                null,
                usuarioDto.getNombre(),
                usuarioDto.getApellido(),
                usuarioDto.getDni(),
                usuario
        );
        organizadorRepository.guardarOrganizadro(organizador);
    }
}
