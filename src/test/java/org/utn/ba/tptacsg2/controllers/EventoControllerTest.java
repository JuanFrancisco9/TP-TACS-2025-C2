package org.utn.ba.tptacsg2.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.utn.ba.tptacsg2.dtos.ParticipanteDTO;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.services.EventoService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventoControllerTest {

    @Mock
    private EventoService eventoService;

    @InjectMocks
    private EventoController eventoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getParticipantesFromEvento_listaVacia() {
        // given
        String eventoId = "1";
        when(eventoService.getParticipantes(eventoId))
                .thenReturn(List.of()); // lista vacía

        // when
        ResponseEntity<?> response = eventoController.getParticipantesFromEvento(eventoId);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List<?>);

        List<?> body = (List<?>) response.getBody();
        assertTrue(body.isEmpty()); // lista vacía

        verify(eventoService, times(1)).getParticipantes(eventoId);
    }

    @Test
    void getParticipantesFromEvento_listaConElementos() {
        // given
        String eventoId = "2";
        Participante participante1 = new Participante("1", "Juan", "Pérez", "12345678");
        Participante participante2 = new Participante("2", "María", "Gómez", "87654321");

        when(eventoService.getParticipantes(eventoId))
                .thenReturn(List.of(participante1, participante2));

        // when
        ResponseEntity<?> response = eventoController.getParticipantesFromEvento(eventoId);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List<?>);

        List<?> body = (List<?>) response.getBody();
        assertEquals(2, body.size());

        ParticipanteDTO dto1 = (ParticipanteDTO) body.get(0);
        ParticipanteDTO dto2 = (ParticipanteDTO) body.get(1);

        assertEquals("Juan", dto1.nombre());
        assertEquals("Pérez", dto1.apellido());
        assertEquals("12345678", dto1.dni());

        assertEquals("María", dto2.nombre());
        assertEquals("Gómez", dto2.apellido());
        assertEquals("87654321", dto2.dni());

        verify(eventoService, times(1)).getParticipantes(eventoId);
    }
}
