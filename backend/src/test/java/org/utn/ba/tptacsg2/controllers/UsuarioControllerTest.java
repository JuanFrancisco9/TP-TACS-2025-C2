package org.utn.ba.tptacsg2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.utn.ba.tptacsg2.dtos.InputRegistroDto;
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.services.UsuarioService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().findAndAddModules().build();
        mockMvc = MockMvcBuilders.standaloneSetup(new UsuarioController(usuarioService))
                .setMessageConverters(
                        new StringHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    @DisplayName("POST /user -> 201 cuando se registra correctamente")
    void registrarUsuario_devuelve201() throws Exception {
        InputRegistroDto dto = new InputRegistroDto("user", "pass", "ROLE_ADMIN", "Ana", "Perez", "123");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuario registrado correctamente"));

        verify(usuarioService).registrar(any(InputRegistroDto.class));
    }

    @Test
    @DisplayName("POST /user -> 400 cuando el servicio lanza excepción")
    void registrarUsuario_devuelve400CuandoOcurreExcepcion() throws Exception {
        InputRegistroDto dto = new InputRegistroDto("user", "pass", "ROLE_ADMIN", "Ana", "Perez", "123");
        doThrow(new RuntimeException("fallo")).when(usuarioService).registrar(any(InputRegistroDto.class));

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error del servidor: fallo"));
    }

    @Test
    @DisplayName("GET /user -> 200 con lista de usuarios")
    void getUsuarios_devuelveLista() throws Exception {
        List<Usuario> usuarios = List.of(
                new Usuario("1", "user1", "hash1", Rol.ROLE_USER),
                new Usuario("2", "user2", "hash2", Rol.ROLE_ADMIN)
        );
        when(usuarioService.getUsuarios()).thenReturn(usuarios);

        mockMvc.perform(get("/user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }
}
