package org.utn.ba.tptacsg2.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.events.Ubicacion;
import org.utn.ba.tptacsg2.models.events.Precio;
import org.utn.ba.tptacsg2.services.OrganizadorService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizadorController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrganizadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrganizadorService organizadorService;

    @Test
    @DisplayName("GET /organizadores/eventos/{id} devuelve 200 y lista cuando hay eventos")
    void getEventosDeOrganizador_devuelve200ConLista() throws Exception {
        String idOrganizador = "1";
        Evento evento1 = new Evento("0","Seminario de Mocks","Mocks", LocalDateTime.now(),"19:00",5F,new Ubicacion("","","", ""),10, 0, new Precio("Pesos",100F),null,new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), null, new ArrayList<>());
        Evento evento2 = new Evento("1","Workshop Testing","Testing", LocalDateTime.now(),"10:00",3F,new Ubicacion("","","", ""),20, 0 ,new Precio("Pesos",150F),null,new EstadoEvento("2", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), null, new ArrayList<>());
        List<Evento> eventos = Arrays.asList(evento1, evento2);

        when(organizadorService.getEventosDeOrganizador(idOrganizador)).thenReturn(eventos);

        mockMvc.perform(get("/organizadores/eventos/{id_organizador}", idOrganizador)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /organizadores/eventos/{id} devuelve 204 cuando no hay eventos")
    void getEventosDeOrganizador_devuelve204SinContenido() throws Exception {
        String idOrganizador = "2";
        when(organizadorService.getEventosDeOrganizador(idOrganizador)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/organizadores/eventos/{id_organizador}", idOrganizador)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

