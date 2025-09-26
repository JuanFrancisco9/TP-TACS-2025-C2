package org.utn.ba.tptacsg2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.utn.ba.tptacsg2.config.TestSecurityConfig;
import org.utn.ba.tptacsg2.dtos.InputRegistroDto;
// IMPORTÁ la clase correcta de tu dominio:
import org.utn.ba.tptacsg2.dtos.LoginResponseDto;
import org.utn.ba.tptacsg2.models.users.Usuario; // <-- ajusta el package si difiere
import org.utn.ba.tptacsg2.services.UsuarioService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(TestSecurityConfig.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /login -> 200 OK cuando las credenciales son válidas")
    void login_devuelve200CuandoCredencialesValidas() throws Exception {
        // Arrange
        InputRegistroDto dto = new InputRegistroDto("testuser", "password123", "USER","","","");
        // Crear un usuario mock con propiedades básicas
        LoginResponseDto mockUsuario = Mockito.mock(LoginResponseDto.class);
        when(usuarioService.login(any(InputRegistroDto.class)))
                .thenReturn(mockUsuario);

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)); // ahora retorna JSON

        verify(usuarioService, times(1)).login(any(InputRegistroDto.class));
    }

    @Test
    @DisplayName("POST /login -> 401 Unauthorized cuando el usuario no existe")
    void login_devuelve401CuandoUsuarioNoExiste() throws Exception {
        // Arrange
        InputRegistroDto dto = new InputRegistroDto("nouser", "whatever", "USER", "","","");
        when(usuarioService.login(any(InputRegistroDto.class)))
                .thenThrow(new UsernameNotFoundException("Usuario no encontrado"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("")); // el controller hace .build() sin body

        verify(usuarioService, times(1)).login(any(InputRegistroDto.class));
    }

    @Test
    @DisplayName("POST /login -> 401 Unauthorized cuando la contraseña es incorrecta")
    void login_devuelve401CuandoPasswordIncorrecta() throws Exception {
        // Arrange
        InputRegistroDto dto = new InputRegistroDto("testuser", "badpass", "USER", "","","");
        when(usuarioService.login(any(InputRegistroDto.class)))
                .thenThrow(new RuntimeException("Contraseña incorrecta"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));

        verify(usuarioService, times(1)).login(any(InputRegistroDto.class));
    }

    @Test
    @DisplayName("POST /login -> 400 Bad Request cuando el body no es JSON válido (opcional)")
    void login_devuelve400CuandoBodyInvalido() throws Exception {
        // Arrange: body inválido
        String invalidJson = "{ username: testuser, password: password123 "; // sin cerrar llaves/quotes

        // Act & Assert
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).login(any(InputRegistroDto.class));
    }
}

