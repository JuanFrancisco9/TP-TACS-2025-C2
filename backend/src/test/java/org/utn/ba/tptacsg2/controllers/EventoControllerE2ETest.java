package org.utn.ba.tptacsg2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockMultipartFile;
import org.utn.ba.tptacsg2.dtos.EventoDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

        SolicitudEvento solicitud = new SolicitudEvento("1",  "Concierto de rock vivo", "Musica", LocalDateTime.of(2025, 9, 10, 20, 0), "20:00", 2f, new Ubicacion("", "", "Buenos Aires", "La Plata", "Teatro Argentino", false, null), 100,0, new Precio("ARS", 1000f), TipoEstadoEvento.CONFIRMADO, categoria1, new ArrayList<>());

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
                solicitud.etiquetas(),
                null
        );

        EventoDTO eventoRespuesta = new EventoDTO(
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
                eventoCreado.estado(),
                eventoCreado.categoria(),
                null,
                eventoCreado.imagenKey()
        );

        when(eventoService.registrarEventoConImagen(any(SolicitudEvento.class), any()))
                .thenReturn(eventoRespuesta);

        MockMultipartFile eventoPart = new MockMultipartFile(
                "evento",
                "evento.json",
                "application/json",
                objectMapper.writeValueAsBytes(solicitud)
        );

        mockMvc.perform(multipart("/eventos").file(eventoPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("EV-001"));
    }

    @Test
    void modificarEvento_deberiaRetornar200YEventoActualizado() throws Exception {
        Categoria categoria1 = new Categoria("Musica");

        SolicitudEvento solicitud = new SolicitudEvento("2", "Festival Jazz", "Musica", LocalDateTime.of(2025, 10, 5, 18, 0), "18:00", 2f, new Ubicacion("", "", "Santa Fe", "Rosario", "Centro Cultural", false, null), 200,0, new Precio("ARS", 2000f), TipoEstadoEvento.PENDIENTE, categoria1,new ArrayList<>());
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
                solicitud.etiquetas(),
                null
        );

        EventoDTO eventoCreadoDTO = new EventoDTO(
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
                eventoCreado.estado(),
                eventoCreado.categoria(),
                null,
                eventoCreado.imagenKey()
        );

        when(eventoService.registrarEventoConImagen(any(SolicitudEvento.class), any()))
                .thenReturn(eventoCreadoDTO);

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
                eventoCreado.etiquetas(),
                null
        );

        when(eventoService.cambiarEstado(eq(eventoCreado.id()), eq(TipoEstadoEvento.CONFIRMADO)))
                .thenReturn(eventoActualizado);

        MockMultipartFile eventoPart = new MockMultipartFile(
                "evento",
                "evento.json",
                "application/json",
                objectMapper.writeValueAsBytes(solicitud)
        );

        mockMvc.perform(multipart("/eventos").file(eventoPart))
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
                new Ubicacion("", "", "Ciudad Autónoma de Buenos Aires", "CABA", "Sala conferencia", false, null),
                10,
                0,
                new Precio("ARS", 500f),
                new Organizador("ORG-100", "Carlos", "Lopez", "333", null),
                new EstadoEvento("EST-100", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                new Categoria("TECNOLOGIA"),
                new ArrayList<>(),
                null
        );

        EventoDTO eventoDto = new EventoDTO(
                evento.id(),
                evento.titulo(),
                evento.descripcion(),
                evento.fecha(),
                evento.horaInicio(),
                evento.duracion(),
                evento.ubicacion(),
                evento.cupoMaximo(),
                evento.cupoMinimo(),
                evento.precio(),
                evento.organizador(),
                evento.estado(),
                evento.categoria(),
                null,
                evento.imagenKey()
        );

        when(eventoService.buscarEventos(any(FiltrosDTO.class)))
                .thenReturn(new ResultadoBusquedaEvento(List.of(eventoDto), 1, 1, 1));

        mockMvc.perform(get("/eventos")
                .param("palabrasClave", "Musica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventos").isArray());
    }

    @Test
    void obtenerEventoPorId_deberiaRetornar200YEvento() throws Exception {
        Evento evento = new Evento(
                "EV-555",
                "Foro de tecnología",
                "Encuentro con especialistas del sector",
                LocalDateTime.of(2026, 3, 15, 10, 0),
                "10:00",
                3.0f,
                new Ubicacion("-34.6", "-58.38", "Buenos Aires", "CABA", "Centro de convenciones", false, null),
                200,
                50,
                new Precio("ARS", 15000f),
                new Organizador("ORG-555", "Laura", "Martinez", "789", null),
                new EstadoEvento("EST-555", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()),
                new Categoria("TECNOLOGIA"),
                List.of("tech", "foro"),
                "imagen-key"
        );

        EventoDTO eventoDto = new EventoDTO(
                evento.id(),
                evento.titulo(),
                evento.descripcion(),
                evento.fecha(),
                evento.horaInicio(),
                evento.duracion(),
                evento.ubicacion(),
                evento.cupoMaximo(),
                evento.cupoMinimo(),
                evento.precio(),
                evento.organizador(),
                evento.estado(),
                evento.categoria(),
                "https://example.com/imagen.jpg",
                evento.imagenKey()
        );

        when(eventoService.obtenerEventoPorId(evento.id())).thenReturn(eventoDto);

        mockMvc.perform(get("/eventos/" + evento.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("EV-555"))
                .andExpect(jsonPath("$.titulo").value("Foro de tecnología"))
                .andExpect(jsonPath("$.imagenUrl").value("https://example.com/imagen.jpg"));
    }
}
