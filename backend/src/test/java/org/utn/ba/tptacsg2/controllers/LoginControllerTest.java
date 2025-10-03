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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.utn.ba.tptacsg2.dtos.InputRegistroDto;
import org.utn.ba.tptacsg2.dtos.LoginResponseDto;
import org.utn.ba.tptacsg2.services.UsuarioService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().findAndAddModules().build();
        mockMvc = MockMvcBuilders.standaloneSetup(new LoginController(usuarioService))
                .setMessageConverters(
                        new StringHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    @DisplayName("POST /login -> 200 OK cuando las credenciales son válidas")
    void login_devuelve200CuandoCredencialesValidas() throws Exception {
        InputRegistroDto dto = new InputRegistroDto("testuser", "password123", "USER","","","");
        LoginResponseDto mockResponse = new LoginResponseDto("123", "testuser", null, null);
        when(usuarioService.login(any(InputRegistroDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(usuarioService, times(1)).login(any(InputRegistroDto.class));
    }

    @Test
    @DisplayName("POST /login -> 401 Unauthorized cuando el usuario no existe")
    void login_devuelve401CuandoUsuarioNoExiste() throws Exception {
        InputRegistroDto dto = new InputRegistroDto("nouser", "whatever", "USER", "","","");
        when(usuarioService.login(any(InputRegistroDto.class)))
                .thenThrow(new UsernameNotFoundException("Usuario no encontrado"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));

        verify(usuarioService, times(1)).login(any(InputRegistroDto.class));
    }

    @Test
    @DisplayName("POST /login -> 401 Unauthorized cuando la contraseña es incorrecta")
    void login_devuelve401CuandoPasswordIncorrecta() throws Exception {
        InputRegistroDto dto = new InputRegistroDto("testuser", "badpass", "USER", "","","");
        when(usuarioService.login(any(InputRegistroDto.class)))
                .thenThrow(new RuntimeException("Contraseña incorrecta"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));

        verify(usuarioService, times(1)).login(any(InputRegistroDto.class));
    }

    @Test
    @DisplayName("POST /login -> 400 Bad Request cuando el body no es JSON válido")
    void login_devuelve400CuandoBodyInvalido() throws Exception {
        String invalidJson = "{ username: testuser, password: password123 ";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).login(any(InputRegistroDto.class));
    }
}
