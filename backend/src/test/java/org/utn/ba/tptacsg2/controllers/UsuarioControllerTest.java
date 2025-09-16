package org.utn.ba.tptacsg2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.utn.ba.tptacsg2.config.TestSecurityConfig;
import org.utn.ba.tptacsg2.dtos.InputRegistroDto;
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.services.UsuarioService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import(TestSecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe devolver 201 cuando se registra un usuario correctamente")
    void registrarUsuario_devuelve201CuandoSeRegistraCorrectamente() throws Exception {
        // Arrange
        InputRegistroDto inputRegistroDto = new InputRegistroDto("testuser", "password123", "PARTICIPANTE", "juan", "Perez", "123");
        doNothing().when(usuarioService).registrar(any(InputRegistroDto.class));

        // Act & Assert
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRegistroDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuario registrado correctamente"));

        verify(usuarioService, times(1)).registrar(any(InputRegistroDto.class));
    }

    @Test
    @DisplayName("Debe devolver 400 cuando ocurre una excepción durante el registro")
    void registrarUsuario_devuelve400CuandoOcurreExcepcion() throws Exception {
        // Arrange
        InputRegistroDto inputRegistroDto = new InputRegistroDto("testuser", "password123", "PARTICIPANTE", "juan", "Perez", "123");
        doThrow(new RuntimeException("El usuario ya existe")).when(usuarioService).registrar(any(InputRegistroDto.class));

        // Act & Assert
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRegistroDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error del servidor: El usuario ya existe"));

        verify(usuarioService, times(1)).registrar(any(InputRegistroDto.class));
    }

    @Test
    @DisplayName("Debe devolver 200 OK con lista de usuarios cuando el usuario es ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getUsuarios_devuelve200YListaDeUsuarios() throws Exception {
        // Arrange
        Usuario usuario1 = new Usuario("1", "user1", "pass1", Rol.ROLE_USER);
        Usuario usuario2 = new Usuario("2", "user2", "pass2", Rol.ROLE_ORGANIZER);
        
        when(usuarioService.getUsuarios())
            .thenReturn(Arrays.asList(usuario1, usuario2));

        // Act & Assert
        mockMvc.perform(get("/user")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));

        verify(usuarioService, times(1)).getUsuarios();
    }

    @Test
    @DisplayName("Debe devolver 200 OK con lista vacía cuando no hay usuarios")
    @WithMockUser(roles = "ADMIN")
    void getUsuarios_devuelve200YListaVacia() throws Exception {
        // Arrange
        when(usuarioService.getUsuarios())
            .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/user")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(usuarioService, times(1)).getUsuarios();
    }

    @Test
    @DisplayName("Debe devolver 403 Forbidden cuando se accede sin rol ADMIN")
    @WithMockUser(roles = "USER")
    void getUsuarios_devuelve403SinRolAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/user")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).getUsuarios();
    }


}