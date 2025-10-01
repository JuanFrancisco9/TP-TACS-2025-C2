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
import org.utn.ba.tptacsg2.dtos.LoginResponseDto;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.repositories.db.OrganizadorRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.ParticipanteRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.UsuarioRepositoryDB;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    UsuarioRepositoryDB usuarioRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock
    ParticipanteRepositoryDB participanteRepository;
    @Mock
    OrganizadorRepositoryDB organizadorRepository;
    @Mock GeneradorIDService generadorIDService;
    @InjectMocks UsuarioService service;

    @Nested
    @DisplayName("registrar")
    class RegistrarTests {

        @Test
        @DisplayName("Lanza RuntimeException si el username ya existe")
        void registrar_usuarioExiste() {
            InputRegistroDto dto = new InputRegistroDto("testuser", "pass", "ADMIN", "","","");
            when(usuarioRepository.findByUsername("testuser"))
                    .thenReturn(Optional.of(mock(Usuario.class)));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.registrar(dto));
            assertEquals("El usuario ya existe", ex.getMessage());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Guarda usuario con password codificada y rol ADMIN")
        void registrar_ok_admin() {
            when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("secret")).thenReturn("ENCODED");

            InputRegistroDto dto = new InputRegistroDto("admin", "secret", "ROLE_ADMIN", "", "", ""); // prueba strip + upper
            service.registrar(dto);

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).save(captor.capture());
            Usuario saved = captor.getValue();

            assertEquals("admin", saved.username());
            assertEquals("ENCODED", saved.passwordHash());
            assertEquals(Rol.ROLE_ADMIN, saved.rol());
        }

        @Test
        @DisplayName("Guarda usuario con rol ORGANIZADOR")
        void registrar_ok_organizador() {
            when(usuarioRepository.findByUsername("orga")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("s")).thenReturn("E");
            InputRegistroDto dto = new InputRegistroDto("orga", "s", "ROLE_ORGANIZER", "", "", "");
            service.registrar(dto);

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
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
            Usuario u1 = new Usuario("1", "user1", "HASH1", Rol.ROLE_USER);
            Usuario u2 = new Usuario("2", "user2", "HASH2", Rol.ROLE_ADMIN);
            when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

            List<Usuario> res = service.getUsuarios();

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
            Usuario stored = new Usuario("7", "testuser", "HASH", Rol.ROLE_USER);
            when(usuarioRepository.findAll()).thenReturn(List.of(stored));
            when(passwordEncoder.matches("password123", "HASH")).thenReturn(true);

            InputRegistroDto dto = new InputRegistroDto("testuser", "password123", "USER", "", "", "");
            LoginResponseDto result = service.login(dto);
            LoginResponseDto expected = new LoginResponseDto("7", "testuser", Rol.ROLE_USER, null);
            assertEquals(expected, result);
            verify(passwordEncoder).matches("password123", "HASH");
        }

        @Test
        @DisplayName("Lanza UsernameNotFoundException si el usuario no existe")
        void login_usuarioNoExiste() {
            when(usuarioRepository.findAll()).thenReturn(List.of()); // no hay usuarios

            InputRegistroDto dto = new InputRegistroDto("nouser", "x", "USER", "", "", "");
            assertThrows(UsernameNotFoundException.class, () -> service.login(dto));
            verify(passwordEncoder, never()).matches(any(), any());
        }

        @Test
        @DisplayName("Lanza RuntimeException si el password es incorrecto")
        void login_passwordIncorrecta() {
            Usuario stored = new Usuario("8", "testuser", "HASH", Rol.ROLE_USER);
            when(usuarioRepository.findAll()).thenReturn(List.of(stored));
            when(passwordEncoder.matches("bad", "HASH")).thenReturn(false);

            InputRegistroDto dto = new InputRegistroDto("testuser", "bad", "USER", "", "", "");
            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.login(dto));
            assertEquals("Contraseña incorrecta", ex.getMessage());
        }
    }
}
