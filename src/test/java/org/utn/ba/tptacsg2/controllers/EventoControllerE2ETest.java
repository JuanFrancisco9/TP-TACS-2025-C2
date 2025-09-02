package org.utn.ba.tptacsg2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.events.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventoControllerE2ETest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearEvento_deberiaRetornar201YEvento() throws Exception {

        Categoria categoria1 = new Categoria("Musica");

        SolicitudEvento solicitud = new SolicitudEvento("1",  "Concierto de rock vivo", "Musica", LocalDateTime.of(2025, 9, 10, 20, 0), "20:00", 2f, new Ubicacion("", "", "La Plata", "CABA"), 100,0, new Precio("ARS", 1000f), TipoEstadoEvento.CONFIRMADO, categoria1, new ArrayList<>());


        mockMvc.perform(post("/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void modificarEvento_deberiaRetornar200YEventoActualizado() throws Exception {
        // Primero, crear un evento
        Categoria categoria1 = new Categoria("Musica");

        SolicitudEvento solicitud = new SolicitudEvento("2", "Festival Jazz", "Musica", LocalDateTime.of(2025, 10, 5, 18, 0), "18:00", 2f, new Ubicacion("", "", "Rosario", "Santa Fe"), 200,0, new Precio("ARS", 2000f), TipoEstadoEvento.PENDIENTE, categoria1,new ArrayList<>());
        String response = mockMvc.perform(post("/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(response).get("id").asText();

        // Modificar el estado del evento
        mockMvc.perform(put("/eventos/" + id + "?estado=CONFIRMADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado.tipoEstado").value("CONFIRMADO"));
    }

    @Test
    void buscarEventos_deberiaRetornar200YLista() throws Exception {
        mockMvc.perform(get("/eventos")
                .param("palabrasClave", "Musica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventos").isArray());
    }
}
