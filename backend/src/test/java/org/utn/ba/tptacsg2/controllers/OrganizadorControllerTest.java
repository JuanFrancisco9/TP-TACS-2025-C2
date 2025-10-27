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
import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.Precio;
import org.utn.ba.tptacsg2.models.events.Ubicacion;
import org.utn.ba.tptacsg2.models.location.Localidad;
import org.utn.ba.tptacsg2.models.location.Provincia;
import org.utn.ba.tptacsg2.services.EventoService;
import org.utn.ba.tptacsg2.services.OrganizadorService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrganizadorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrganizadorService organizadorService;
    @Mock
    private EventoService eventoService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrganizadorController(organizadorService, eventoService)).build();
    }

    @Test
    @DisplayName("GET /organizadores/eventos/{id} devuelve 200 y lista cuando hay eventos")
    void getEventosDeOrganizador_devuelve200ConLista() throws Exception {
        String idOrganizador = "1";
        Evento evento1 = new Evento("0","Seminario de Mocks","Mocks", LocalDateTime.now(),"19:00",5F,new Ubicacion("","", "Buenos Aires", "La Plata", "Universidad", false, null),10, 0, new Precio("Pesos",100F),null,new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), null, new ArrayList<>(), null, LocalDateTime.now());
        Evento evento2 = new Evento("1","Workshop Testing","Testing", LocalDateTime.now(),"10:00",3F,new Ubicacion("","", "Ciudad Autónoma de Buenos Aires", "CABA", "Auditorio", false, null),20, 0 ,new Precio("Pesos",150F),null,new EstadoEvento("2", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), null, new ArrayList<>(), null, LocalDateTime.now());
        List<Evento> eventos = Arrays.asList(evento1, evento2);

        when(organizadorService.getEventosDeOrganizador(idOrganizador)).thenReturn(eventos);

        mockMvc.perform(get("/organizadores/eventos/{idUsuario}", idOrganizador)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /organizadores/eventos/{id} devuelve 204 cuando no hay eventos")
    void getEventosDeOrganizador_devuelve204SinContenido() throws Exception {
        String idOrganizador = "2";
        when(organizadorService.getEventosDeOrganizador(idOrganizador)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/organizadores/eventos/{idUsuario}", idOrganizador)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
