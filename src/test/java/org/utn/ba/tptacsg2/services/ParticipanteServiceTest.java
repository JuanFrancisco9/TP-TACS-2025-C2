package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipanteServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    private ParticipanteService participanteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        participanteService = new ParticipanteService(inscripcionRepository);
    }

    @Test
    @DisplayName("Debe devolver lista de inscripciones cuando el participante tiene inscripciones")
    void getInscripcionesDeParticipante_devuelveListaDeInscripciones() {

        String idParticipante = "participante1";
        Inscripcion inscripcion1 = mock(Inscripcion.class);
        Inscripcion inscripcion2 = mock(Inscripcion.class);
        List<Inscripcion> inscripcionesEsperadas = Arrays.asList(inscripcion1, inscripcion2);

        when(inscripcionRepository.getInscripcionesDeParticipante(idParticipante))
            .thenReturn(inscripcionesEsperadas);


        List<Inscripcion> inscripciones = participanteService.getInscripcionesDeParticipante(idParticipante);


        assertEquals(2, inscripciones.size());
        assertEquals(inscripcionesEsperadas, inscripciones);
        verify(inscripcionRepository).getInscripcionesDeParticipante(idParticipante);
    }

    @Test
    @DisplayName("Debe devolver lista vacía cuando el participante no tiene inscripciones")
    void getInscripcionesDeParticipante_devuelveListaVaciaSiNoHayInscripciones() {

        String idParticipante = "participante2";
        when(inscripcionRepository.getInscripcionesDeParticipante(idParticipante))
            .thenReturn(Collections.emptyList());


        List<Inscripcion> inscripciones = participanteService.getInscripcionesDeParticipante(idParticipante);


        assertTrue(inscripciones.isEmpty());
        verify(inscripcionRepository).getInscripcionesDeParticipante(idParticipante);
    }
}
