package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.utn.ba.tptacsg2.dtos.InputRegistroDto;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.repositories.OrganizadorRepository;
import org.utn.ba.tptacsg2.repositories.ParticipanteRepository;
import org.utn.ba.tptacsg2.repositories.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UsuarioRepository usuarioRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock ParticipanteRepository participanteRepository;
    @Mock OrganizadorRepository organizadorRepository;
    @Mock GeneradorIDService generadorIDService;
    @InjectMocks UsuarioService service;

    @Nested
    @DisplayName("registrar")
    class RegistrarTests {

        @Test
        @DisplayName("Lanza RuntimeException si el username ya existe")
        void registrar_usuarioExiste() {
            var dto = new InputRegistroDto("testuser", "pass", "ADMIN", "","","");
            when(usuarioRepository.findByUsername("testuser"))
                    .thenReturn(Optional.of(mock(Usuario.class)));

            var ex = assertThrows(RuntimeException.class, () -> service.registrar(dto));
            assertEquals("El usuario ya existe", ex.getMessage());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Guarda usuario con password codificada y rol ADMIN")
        void registrar_ok_admin() {
            when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("secret")).thenReturn("ENCODED");

            var dto = new InputRegistroDto("admin", "secret", "ROLE_ADMIN", "", "", ""); // prueba strip + upper
            service.registrar(dto);

            var captor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).save(captor.capture());
            var saved = captor.getValue();

            assertEquals("admin", saved.username());
            assertEquals("ENCODED", saved.passwordHash());
            assertEquals(Rol.ROLE_ADMIN, saved.rol());
        }

        @Test
        @DisplayName("Guarda usuario con rol ORGANIZADOR")
        void registrar_ok_organizador() {
            when(usuarioRepository.findByUsername("orga")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("s")).thenReturn("E");
            var dto = new InputRegistroDto("orga", "s", "ROLE_ORGANIZER", "", "", "");
            service.registrar(dto);

            var captor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).save(captor.capture());
            assertEquals(Rol.ROLE_ORGANIZER, captor.getValue().rol());
        }
    }

    @Nested
    @DisplayName("getUsuarios")
    class GetUsuariosTests {

        @Test
        @DisplayName("Devuelve lista mapeada correctamente a UsuarioDto")
        void getUsuarios_ok() {
            var u1 = new Usuario("1", "user1", "HASH1", Rol.ROLE_USER);
            var u2 = new Usuario("2", "user2", "HASH2", Rol.ROLE_ADMIN);
            when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

            var res = service.getUsuarios();

            assertEquals(2, res.size());
            assertEquals("1", res.get(0).id());
            assertEquals("user1", res.get(0).username());
            // En tu service, el dto.password lleva el passwordHash:
            assertEquals("HASH1", res.get(0).passwordHash());
            assertEquals("ROLE_USER", res.get(0).rol().toString());

            assertEquals("2", res.get(1).id());
            assertEquals("user2", res.get(1).username());
            assertEquals("HASH2", res.get(1).passwordHash());
            assertEquals("ROLE_ADMIN", res.get(1).rol().toString());
        }
    }

    @Nested
    @DisplayName("login")
    class LoginTests {

        @Test
        @DisplayName("Devuelve Usuario cuando username existe y el password coincide")
        void login_ok() {
            var stored = new Usuario("7", "testuser", "HASH", Rol.ROLE_USER);
            when(usuarioRepository.findAll()).thenReturn(List.of(stored));
            when(passwordEncoder.matches("password123", "HASH")).thenReturn(true);

            var dto = new InputRegistroDto("testuser", "password123", "USER", "", "", "");
            var result = service.login(dto);

            assertSame(stored, result);
            verify(passwordEncoder).matches("password123", "HASH");
        }

        @Test
        @DisplayName("Lanza UsernameNotFoundException si el usuario no existe")
        void login_usuarioNoExiste() {
            when(usuarioRepository.findAll()).thenReturn(List.of()); // no hay usuarios

            var dto = new InputRegistroDto("nouser", "x", "USER", "", "", "");
            assertThrows(UsernameNotFoundException.class, () -> service.login(dto));
            verify(passwordEncoder, never()).matches(any(), any());
        }

        @Test
        @DisplayName("Lanza RuntimeException si el password es incorrecto")
        void login_passwordIncorrecta() {
            var stored = new Usuario("8", "testuser", "HASH", Rol.ROLE_USER);
            when(usuarioRepository.findAll()).thenReturn(List.of(stored));
            when(passwordEncoder.matches("bad", "HASH")).thenReturn(false);

            var dto = new InputRegistroDto("testuser", "bad", "USER", "", "", "");
            var ex = assertThrows(RuntimeException.class, () -> service.login(dto));
            assertEquals("Contraseña incorrecta", ex.getMessage());
        }
    }
}
