package org.utn.ba.tptacsg2.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.utn.ba.tptacsg2.dtos.EstadisticasUsoDTO;
import org.utn.ba.tptacsg2.enums.TipoEstadistica;
import org.utn.ba.tptacsg2.services.EstadisticasService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EstadisticasController.class)
@AutoConfigureMockMvc(addFilters = false)
class EstadisticasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstadisticasService estadisticasService;

    @Test
    @DisplayName("GET /estadisticas/uso debe retornar 200 con estadísticas")
    void obtenerEstadisticasUso_DebeRetornar200ConEstadisticas() throws Exception {

        EstadisticasUsoDTO estadisticasMock = new EstadisticasUsoDTO(
            15,
            8,
            120,
            95,
            25,
            60.0,
            "TP TACS G2",
            8.0
        );

        when(estadisticasService.obtenerEstadisticasUso(any(), any(), any())).thenReturn(estadisticasMock);

        mockMvc.perform(get("/estadisticas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cantidad_eventos").value(15))
                .andExpect(jsonPath("$.cantidad_eventos_activos").value(8))
                .andExpect(jsonPath("$.cantidad_inscripciones_totales").value(120))
                .andExpect(jsonPath("$.cantidad_inscripciones_confirmadas").value(95))
                .andExpect(jsonPath("$.cantidad_inscripciones_waitlist").value(25))
                .andExpect(jsonPath("$.tasa_conversion_waitlist").value(60.0))
                .andExpect(jsonPath("$.evento_mas_popular").value("TP TACS G2"))
                .andExpect(jsonPath("$.promedio_inscripciones_por_evento").value(8.0));
    }

    @Test
    @DisplayName("GET /estadisticas/uso debe tener Content-Type correcto")
    void obtenerEstadisticasUso_DebeTenerContentTypeJson() throws Exception {

        EstadisticasUsoDTO estadisticasMock = new EstadisticasUsoDTO(
            10, 5, 100, 80, 20, 50.0, "Evento Test", 10.0
        );

        when(estadisticasService.obtenerEstadisticasUso(any(), any(), any())).thenReturn(estadisticasMock);


        mockMvc.perform(get("/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"));
    }

    @Test
    @DisplayName("GET /estadisticas/eventos/cantidad debe retornar cantidad de eventos")
    void obtenerCantidadEventos_DebeRetornar200ConCantidad() throws Exception {
        when(estadisticasService.obtenerCantidadEventos(any(), any())).thenReturn(15);

        mockMvc.perform(get("/estadisticas/eventos/cantidad")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("15"));
    }

    @Test
    @DisplayName("GET /estadisticas/eventos/activos debe retornar eventos activos")
    void obtenerCantidadEventosActivos_DebeRetornar200ConCantidad() throws Exception {
        when(estadisticasService.obtenerCantidadEventosActivos(any(), any())).thenReturn(8);

        mockMvc.perform(get("/estadisticas/eventos/activos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("8"));
    }

    @Test
    @DisplayName("GET /estadisticas/inscripciones/totales debe retornar total inscripciones")
    void obtenerCantidadInscripcionesTotales_DebeRetornar200ConCantidad() throws Exception {
        when(estadisticasService.obtenerCantidadInscripcionesTotales(any(), any())).thenReturn(120);

        mockMvc.perform(get("/estadisticas/inscripciones/totales")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("120"));
    }

    @Test
    @DisplayName("GET /estadisticas/inscripciones/confirmadas debe retornar inscripciones confirmadas")
    void obtenerCantidadInscripcionesConfirmadas_DebeRetornar200ConCantidad() throws Exception {
        when(estadisticasService.obtenerCantidadInscripcionesConfirmadas(any(), any())).thenReturn(95);

        mockMvc.perform(get("/estadisticas/inscripciones/confirmadas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("95"));
    }

    @Test
    @DisplayName("GET /estadisticas/inscripciones/waitlist debe retornar inscripciones en waitlist")
    void obtenerCantidadInscripcionesWaitlist_DebeRetornar200ConCantidad() throws Exception {
        when(estadisticasService.obtenerCantidadInscripcionesWaitlist(any(), any())).thenReturn(25);

        mockMvc.perform(get("/estadisticas/inscripciones/waitlist")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("25"));
    }

    @Test
    @DisplayName("GET /estadisticas/conversion/waitlist debe retornar tasa de conversion")
    void obtenerTasaConversionWaitlist_DebeRetornar200ConTasa() throws Exception {
        when(estadisticasService.obtenerTasaConversionWaitlist(any(), any())).thenReturn(60.0);

        mockMvc.perform(get("/estadisticas/conversion/waitlist")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("60.0"));
    }

    @Test
    @DisplayName("GET /estadisticas/eventos/mas-popular debe retornar evento mas popular")
    void obtenerEventoMasPopular_DebeRetornar200ConEvento() throws Exception {
        when(estadisticasService.obtenerEventoMasPopular(any(), any())).thenReturn("TP TACS G2");

        mockMvc.perform(get("/estadisticas/eventos/mas-popular")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("TP TACS G2"));
    }

    @Test
    @DisplayName("GET /estadisticas/inscripciones/promedio-por-evento debe retornar promedio")
    void obtenerPromedioInscripcionesPorEvento_DebeRetornar200ConPromedio() throws Exception {
        when(estadisticasService.obtenerPromedioInscripcionesPorEvento(any(), any())).thenReturn(8.0);

        mockMvc.perform(get("/estadisticas/inscripciones/promedio-por-evento")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("8.0"));
    }

    @Test
    @DisplayName("GET /estadisticas con filtro de fechas debe retornar estadísticas filtradas")
    void obtenerEstadisticasUsoConFiltroFechas_DebeRetornar200() throws Exception {
        EstadisticasUsoDTO estadisticasMock = new EstadisticasUsoDTO(
            5, 3, 50, 40, 10, 80.0, "Evento Filtrado", 10.0
        );

        when(estadisticasService.obtenerEstadisticasUso(any(LocalDate.class), any(LocalDate.class), any()))
                .thenReturn(estadisticasMock);

        mockMvc.perform(get("/estadisticas")
                .param("fechaDesde", "2024-01-01")
                .param("fechaHasta", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cantidad_eventos").value(5))
                .andExpect(jsonPath("$.cantidad_eventos_activos").value(3))
                .andExpect(jsonPath("$.evento_mas_popular").value("Evento Filtrado"));
    }

    @Test
    @DisplayName("GET /estadisticas con estadísticas específicas debe retornar solo las solicitadas")
    void obtenerEstadisticasUsoConEstadisticasEspecificas_DebeRetornar200() throws Exception {
        EstadisticasUsoDTO estadisticasMock = new EstadisticasUsoDTO(
            15, null, null, null, null, null, null, null
        );

        when(estadisticasService.obtenerEstadisticasUso(eq(null), eq(null), any(Set.class)))
                .thenReturn(estadisticasMock);

        mockMvc.perform(get("/estadisticas")
                .param("estadisticas", "CANTIDAD_EVENTOS")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cantidad_eventos").value(15))
                .andExpect(jsonPath("$.cantidad_eventos_activos").doesNotExist());
    }

    @Test
    @DisplayName("GET /estadisticas con múltiples estadísticas debe retornar las solicitadas")
    void obtenerEstadisticasUsoConMultiplesEstadisticas_DebeRetornar200() throws Exception {
        EstadisticasUsoDTO estadisticasMock = new EstadisticasUsoDTO(
            15, 8, null, null, null, null, null, null
        );

        when(estadisticasService.obtenerEstadisticasUso(eq(null), eq(null), any(Set.class)))
                .thenReturn(estadisticasMock);

        mockMvc.perform(get("/estadisticas")
                .param("estadisticas", "CANTIDAD_EVENTOS,CANTIDAD_EVENTOS_ACTIVOS")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cantidad_eventos").value(15))
                .andExpect(jsonPath("$.cantidad_eventos_activos").value(8))
                .andExpect(jsonPath("$.cantidad_inscripciones_totales").doesNotExist());
    }

    @Test
    @DisplayName("GET /estadisticas/completas debe retornar todas las estadísticas")
    void obtenerTodasLasEstadisticas_DebeRetornar200ConTodasLasEstadisticas() throws Exception {
        EstadisticasUsoDTO estadisticasMock = new EstadisticasUsoDTO(
            10, 5, 100, 80, 20, 50.0, "Evento Completo", 10.0
        );

        when(estadisticasService.obtenerEstadisticasUso(any(), any(), eq(null)))
                .thenReturn(estadisticasMock);

        mockMvc.perform(get("/estadisticas/completas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cantidad_eventos").value(10))
                .andExpect(jsonPath("$.cantidad_eventos_activos").value(5))
                .andExpect(jsonPath("$.cantidad_inscripciones_totales").value(100))
                .andExpect(jsonPath("$.evento_mas_popular").value("Evento Completo"));
    }

    @Test
    @DisplayName("GET /estadisticas/completas con filtros de fecha debe retornar estadísticas filtradas")
    void obtenerTodasLasEstadisticasConFiltros_DebeRetornar200() throws Exception {
        EstadisticasUsoDTO estadisticasMock = new EstadisticasUsoDTO(
            3, 2, 30, 25, 5, 83.33, "Evento Filtrado", 10.0
        );

        when(estadisticasService.obtenerEstadisticasUso(any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(estadisticasMock);

        mockMvc.perform(get("/estadisticas/completas")
                .param("fechaDesde", "2024-06-01")
                .param("fechaHasta", "2024-06-30")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cantidad_eventos").value(3))
                .andExpect(jsonPath("$.cantidad_eventos_activos").value(2))
                .andExpect(jsonPath("$.evento_mas_popular").value("Evento Filtrado"));
    }

    @Test
    @DisplayName("GET /estadisticas/eventos/cantidad con filtros debe retornar cantidad filtrada")
    void obtenerCantidadEventosConFiltros_DebeRetornar200() throws Exception {
        when(estadisticasService.obtenerCantidadEventos(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(5);

        mockMvc.perform(get("/estadisticas/eventos/cantidad")
                .param("fechaDesde", "2024-01-01")
                .param("fechaHasta", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("5"));
    }
}
