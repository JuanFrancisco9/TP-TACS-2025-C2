package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.utn.ba.tptacsg2.dtos.EventoDTO;
import org.utn.ba.tptacsg2.dtos.InscripcionDTO;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.repositories.db.InscripcionRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.ParticipanteRepositoryDB;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipanteServiceTest {

    @Mock
    private InscripcionRepositoryDB inscripcionRepository;

    @Mock
    private EventoService eventoService;

    private ParticipanteService participanteService;
    private ParticipanteRepositoryDB participanteRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        participanteService = new ParticipanteService(inscripcionRepository, eventoService, participanteRepository);
    }

    @Test
    @DisplayName("Debe devolver lista de inscripciones cuando el participante tiene inscripciones")
    void getInscripcionesDeParticipante_devuelveListaDeInscripciones() {

        String idParticipante = "1";
        Inscripcion inscripcion1 = mock(Inscripcion.class);
        Inscripcion inscripcion2 = mock(Inscripcion.class);

        Evento evento1 = mock(Evento.class);
        Evento evento2 = mock(Evento.class);
        EventoDTO eventoDTO1 = new EventoDTO("evento1", null, null, null, null, null, null, null, null, null, null, null, null, null, null,null);
        EventoDTO eventoDTO2 = new EventoDTO("evento2", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        LocalDateTime fechaRegistro1 = LocalDateTime.now();
        LocalDateTime fechaRegistro2 = fechaRegistro1.plusDays(1);

        when(inscripcion1.id()).thenReturn("inscripcion1");
        when(inscripcion1.participante()).thenReturn(null);
        when(inscripcion1.fechaRegistro()).thenReturn(fechaRegistro1);
        when(inscripcion1.estado()).thenReturn(null);
        when(inscripcion1.evento()).thenReturn(evento1);

        when(inscripcion2.id()).thenReturn("inscripcion2");
        when(inscripcion2.participante()).thenReturn(null);
        when(inscripcion2.fechaRegistro()).thenReturn(fechaRegistro2);
        when(inscripcion2.estado()).thenReturn(null);
        when(inscripcion2.evento()).thenReturn(evento2);

        when(eventoService.mapearEventoDTO(evento1)).thenReturn(eventoDTO1);
        when(eventoService.mapearEventoDTO(evento2)).thenReturn(eventoDTO2);

        when(inscripcionRepository.findByParticipante_Id(idParticipante))
            .thenReturn(List.of(inscripcion1, inscripcion2));

        List<InscripcionDTO> inscripciones = participanteService.getInscripcionesDeParticipante(idParticipante);


        assertEquals(2, inscripciones.size());
        assertEquals("inscripcion1", inscripciones.get(0).id());
        assertEquals(eventoDTO1, inscripciones.get(0).evento());
        assertEquals("inscripcion2", inscripciones.get(1).id());
        assertEquals(eventoDTO2, inscripciones.get(1).evento());
        verify(inscripcionRepository).findByParticipante_Id(idParticipante);
        verify(eventoService).mapearEventoDTO(evento1);
        verify(eventoService).mapearEventoDTO(evento2);
        verifyNoMoreInteractions(eventoService);
    }

    @Test
    @DisplayName("Debe devolver lista vacía cuando el participante no tiene inscripciones")
    void getInscripcionesDeParticipante_devuelveListaVaciaSiNoHayInscripciones() {

        String idParticipante = "2";
        when(inscripcionRepository.findByParticipante_Id(idParticipante))
            .thenReturn(Collections.emptyList());


        List<InscripcionDTO> inscripciones = participanteService.getInscripcionesDeParticipante(idParticipante);


        assertTrue(inscripciones.isEmpty());
        verify(inscripcionRepository).findByParticipante_Id(idParticipante);
        verifyNoInteractions(eventoService);
    }
}
