package org.utn.ba.tptacsg2.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.utn.ba.tptacsg2.services.EventoService;

@Component
public class EventoScheduler {

    private final EventoService eventoService;

    @Autowired
    public EventoScheduler(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cerrarEventosProximos() {
        eventoService.cerrarEventosProximos();
    }
}