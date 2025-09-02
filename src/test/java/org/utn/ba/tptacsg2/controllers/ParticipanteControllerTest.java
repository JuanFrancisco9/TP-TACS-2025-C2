package org.utn.ba.tptacsg2.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.services.ParticipanteService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParticipanteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ParticipanteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParticipanteService participanteService;

    @Test
    @DisplayName("Debe devolver 200 OK con lista de inscripciones cuando el participante tiene inscripciones")
    void getInscripcionesDeUsuario_devuelve200YListaDeInscripciones() throws Exception {

        String idUsuario = "usuario1";
        Inscripcion inscripcion1 = org.mockito.Mockito.mock(Inscripcion.class);
        Inscripcion inscripcion2 = org.mockito.Mockito.mock(Inscripcion.class);

        when(participanteService.getInscripcionesDeParticipante(idUsuario))
            .thenReturn(Arrays.asList(inscripcion1, inscripcion2));

        mockMvc.perform(get("/participantes/inscripciones/{id_usuario}", idUsuario)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe devolver 204 No Content cuando el participante no tiene inscripciones")
    void getInscripcionesDeUsuario_devuelve204SiNoHayInscripciones() throws Exception {

        String idUsuario = "usuario2";
        when(participanteService.getInscripcionesDeParticipante(idUsuario))
            .thenReturn(Collections.emptyList());


        mockMvc.perform(get("/participantes/inscripciones/{id_usuario}", idUsuario)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
