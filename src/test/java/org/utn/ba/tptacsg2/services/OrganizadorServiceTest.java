package org.utn.ba.tptacsg2.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.repositories.EventoRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizadorServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    private OrganizadorService organizadorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        organizadorService = new OrganizadorService(eventoRepository);
    }

    @Test
    @DisplayName("Debe devolver lista de eventos cuando el organizador tiene eventos")
    void getEventosDeOrganizador_devuelveListaDeEventos() {

        String idOrganizador = "organizador1";
        Evento evento1 = mock(Evento.class);
        Evento evento2 = mock(Evento.class);
        List<Evento> eventosEsperados = Arrays.asList(evento1, evento2);

        when(eventoRepository.getEventosDeOrganizador(idOrganizador))
            .thenReturn(eventosEsperados);


        List<Evento> eventos = organizadorService.getEventosDeOrganizador(idOrganizador);


        assertEquals(2, eventos.size());
        assertEquals(eventosEsperados, eventos);
        verify(eventoRepository).getEventosDeOrganizador(idOrganizador);
    }

    @Test
    @DisplayName("Debe devolver lista vacía cuando el organizador no tiene eventos")
    void getEventosDeOrganizador_devuelveListaVaciaSiNoHayEventos() {

        String idOrganizador = "organizador2";
        when(eventoRepository.getEventosDeOrganizador(idOrganizador))
            .thenReturn(Collections.emptyList());


        List<Evento> eventos = organizadorService.getEventosDeOrganizador(idOrganizador);


        assertTrue(eventos.isEmpty());
        verify(eventoRepository).getEventosDeOrganizador(idOrganizador);
    }
}
