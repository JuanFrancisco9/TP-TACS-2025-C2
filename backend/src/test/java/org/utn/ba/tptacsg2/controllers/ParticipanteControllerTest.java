package org.utn.ba.tptacsg2.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.services.ParticipanteService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ParticipanteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ParticipanteService participanteService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ParticipanteController(participanteService)).build();
    }

    @Test
    @DisplayName("Debe devolver 200 OK con lista de inscripciones cuando el participante tiene inscripciones")
    void getInscripcionesDeUsuario_devuelve200YListaDeInscripciones() throws Exception {

        String idUsuario = "usuario1";
        Inscripcion inscripcion1 = org.mockito.Mockito.mock(Inscripcion.class);
        Inscripcion inscripcion2 = org.mockito.Mockito.mock(Inscripcion.class);

        when(participanteService.getInscripcionesDeParticipante(idUsuario))
            .thenReturn(Arrays.asList(inscripcion1, inscripcion2));

        mockMvc.perform(get("/participantes/inscripciones/{idUsuario}", idUsuario)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe devolver 204 No Content cuando el participante no tiene inscripciones")
    void getInscripcionesDeUsuario_devuelve204SinInscripciones() throws Exception {

        String idUsuario = "usuario2";
        when(participanteService.getInscripcionesDeParticipante(idUsuario))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/participantes/inscripciones/{idUsuario}", idUsuario)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
