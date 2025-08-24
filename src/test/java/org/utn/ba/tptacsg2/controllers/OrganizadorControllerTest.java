package org.utn.ba.tptacsg2.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.services.OrganizadorService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizadorController.class)
class OrganizadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrganizadorService organizadorService;

    @Test
    @DisplayName("Debe devolver 200 OK con lista de eventos cuando el organizador tiene eventos")
    void getEventosDeOrganizador_devuelve200YListaDeEventos() throws Exception {

        String idOrganizador = "organizador1";
        Evento evento1 = org.mockito.Mockito.mock(Evento.class);
        Evento evento2 = org.mockito.Mockito.mock(Evento.class);

        when(organizadorService.getEventosDeOrganizador(idOrganizador))
            .thenReturn(Arrays.asList(evento1, evento2));


        mockMvc.perform(get("/organizadores/eventos/{id_organizador}", idOrganizador)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe devolver 204 No Content cuando el organizador no tiene eventos")
    void getEventosDeOrganizador_devuelve204SiNoHayEventos() throws Exception {

        String idOrganizador = "organizador2";
        when(organizadorService.getEventosDeOrganizador(idOrganizador))
            .thenReturn(Collections.emptyList());


        mockMvc.perform(get("/organizadores/eventos/{id_organizador}", idOrganizador)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
