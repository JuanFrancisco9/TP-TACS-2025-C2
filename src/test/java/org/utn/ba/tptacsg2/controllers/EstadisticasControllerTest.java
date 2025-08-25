package org.utn.ba.tptacsg2.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.utn.ba.tptacsg2.dtos.EstadisticasUso;
import org.utn.ba.tptacsg2.services.EstadisticasService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EstadisticasController.class)
class EstadisticasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstadisticasService estadisticasService;

    @Test
    @DisplayName("GET /estadisticas/uso debe retornar 200 con estadísticas")
    void obtenerEstadisticasUso_DebeRetornar200ConEstadisticas() throws Exception {

        EstadisticasUso estadisticasMock = new EstadisticasUso(
            15L,
            8L,
            120L,
            95L,
            25L,
            60.0,
            "TP TACS G2",
            8.0
        );

        when(estadisticasService.obtenerEstadisticasUso()).thenReturn(estadisticasMock);

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

        EstadisticasUso estadisticasMock = new EstadisticasUso(
            10L, 5L, 100L, 80L, 20L, 50.0, "Evento Test", 10.0
        );

        when(estadisticasService.obtenerEstadisticasUso()).thenReturn(estadisticasMock);


        mockMvc.perform(get("/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"));
    }
}
