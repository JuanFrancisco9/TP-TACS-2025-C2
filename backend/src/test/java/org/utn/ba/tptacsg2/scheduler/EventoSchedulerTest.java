package org.utn.ba.tptacsg2.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.utn.ba.tptacsg2.services.EventoService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventoSchedulerTest {

    @Mock
    private EventoService eventoService;

    @InjectMocks
    private EventoScheduler eventoScheduler;

    @Test
    public void cerrarEventosProximos_llamaAlServicio() {
        eventoScheduler.cerrarEventosProximos();

        verify(eventoService).cerrarEventosProximos();
    }
}