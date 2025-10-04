package org.utn.ba.tptacsg2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.utn.ba.tptacsg2.dtos.FiltrosDTO;
import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;
import org.utn.ba.tptacsg2.dtos.output.ResultadoBusquedaEvento;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.events.Categoria;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.Precio;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
import org.utn.ba.tptacsg2.models.events.Ubicacion;
import org.utn.ba.tptacsg2.services.EventoService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventoControllerE2ETest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EventoService eventoService;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().findAndAddModules().build();
        mockMvc = MockMvcBuilders.standaloneSetup(new EventoController(eventoService))
                .setMessageConverters(
                        new StringHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void crearEvento_deberiaRetornar201YEvento() throws Exception {

        Categoria categoria1 = new Categoria("Musica");

        SolicitudEvento solicitud = new SolicitudEvento("1",  "Concierto de rock vivo", "Musica", LocalDateTime.of(2025, 9, 10, 20, 0), "20:00", 2f, new Ubicacion("", "", "La Plata", "CABA"), 100,0, new Precio("ARS", 1000f), TipoEstadoEvento.CONFIRMADO, categoria1, new ArrayList<>());

        Evento eventoCreado = new Evento(
                "EV-001",
                solicitud.titulo(),
                solicitud.descripcion(),
                solicitud.fecha(),
                solicitud.horaInicio(),
                solicitud.duracion(),
                solicitud.ubicacion(),
                solicitud.cupoMaximo(),
                solicitud.cupoMinimo(),
                solicitud.precio(),
                new Organizador("ORG-1", "Juan", "Perez", "123", null),
                new EstadoEvento("EST-1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                solicitud.categoria(),
                solicitud.etiquetas()
        );

        when(eventoService.registrarEvento(any(SolicitudEvento.class)))
                .thenReturn(eventoCreado);

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("EV-001"));
    }

    @Test
    void modificarEvento_deberiaRetornar200YEventoActualizado() throws Exception {
        Categoria categoria1 = new Categoria("Musica");

        SolicitudEvento solicitud = new SolicitudEvento("2", "Festival Jazz", "Musica", LocalDateTime.of(2025, 10, 5, 18, 0), "18:00", 2f, new Ubicacion("", "", "Rosario", "Santa Fe"), 200,0, new Precio("ARS", 2000f), TipoEstadoEvento.PENDIENTE, categoria1,new ArrayList<>());
        Evento eventoCreado = new Evento(
                "EV-010",
                solicitud.titulo(),
                solicitud.descripcion(),
                solicitud.fecha(),
                solicitud.horaInicio(),
                solicitud.duracion(),
                solicitud.ubicacion(),
                solicitud.cupoMaximo(),
                solicitud.cupoMinimo(),
                solicitud.precio(),
                new Organizador("ORG-10", "Ana", "Gomez", "222", null),
                new EstadoEvento("EST-10", TipoEstadoEvento.PENDIENTE, LocalDateTime.now()),
                solicitud.categoria(),
                solicitud.etiquetas()
        );

        when(eventoService.registrarEvento(any(SolicitudEvento.class)))
                .thenReturn(eventoCreado);

        Evento eventoActualizado = new Evento(
                eventoCreado.id(),
                eventoCreado.titulo(),
                eventoCreado.descripcion(),
                eventoCreado.fecha(),
                eventoCreado.horaInicio(),
                eventoCreado.duracion(),
                eventoCreado.ubicacion(),
                eventoCreado.cupoMaximo(),
                eventoCreado.cupoMinimo(),
                eventoCreado.precio(),
                eventoCreado.organizador(),
                new EstadoEvento("EST-11", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                eventoCreado.categoria(),
                eventoCreado.etiquetas()
        );

        when(eventoService.cambiarEstado(eq(eventoCreado.id()), eq(TipoEstadoEvento.CONFIRMADO)))
                .thenReturn(eventoActualizado);

        mockMvc.perform(post("/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/eventos/" + eventoCreado.id() + "?estado=CONFIRMADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado.tipoEstado").value("CONFIRMADO"));
    }

    @Test
    void buscarEventos_deberiaRetornar200YLista() throws Exception {
        Evento evento = new Evento(
                "EV-100",
                "Conferencia",
                "Descripcion",
                LocalDateTime.now(),
                "20:00",
                1.0f,
                new Ubicacion("", "", "CABA", ""),
                10,
                0,
                new Precio("ARS", 500f),
                new Organizador("ORG-100", "Carlos", "Lopez", "333", null),
                new EstadoEvento("EST-100", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                new Categoria("TECNOLOGIA"),
                new ArrayList<>()
        );

        when(eventoService.buscarEventos(any(FiltrosDTO.class)))
                .thenReturn(new ResultadoBusquedaEvento(List.of(evento), 1, 1, 1));

        mockMvc.perform(get("/eventos")
                .param("palabrasClave", "Musica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventos").isArray());
    }
}
