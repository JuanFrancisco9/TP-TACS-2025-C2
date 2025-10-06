package org.utn.ba.tptacsg2.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.repositories.db.EventoRepositoryDB;

import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Component
public class EventoRedisCacheInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventoRedisCacheInitializer.class);
    private static final Set<TipoEstadoEvento> ESTADOS_CACHEABLES =
            EnumSet.of(TipoEstadoEvento.CONFIRMADO, TipoEstadoEvento.PENDIENTE);

    private final EventoRepositoryDB eventoRepository;
    private final EventoService eventoService;
    private final RedisCacheService redisCacheService;

    public EventoRedisCacheInitializer(EventoRepositoryDB eventoRepository,
                                       EventoService eventoService,
                                       RedisCacheService redisCacheService) {
        this.eventoRepository = eventoRepository;
        this.eventoService = eventoService;
        this.redisCacheService = redisCacheService;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Evento> eventos = eventoRepository.findAll();
        if (eventos.isEmpty()) {
            LOGGER.debug("No hay eventos persistidos para inicializar en Redis.");
            return;
        }

        int inicializados = 0;
        int omitidos = 0;

        for (Evento evento : eventos) {
            if (!esCacheable(evento)) {
                omitidos++;
                continue;
            }

            try {
                int cuposDisponibles = Math.max(0, eventoService.cuposDisponibles(evento));
                Duration ttl = eventoService.fechaExpiracionDeCache(evento);
                redisCacheService.crearEventoConCupos(evento.id(), cuposDisponibles, ttl);
                inicializados++;
            } catch (Exception e) {
                omitidos++;
                LOGGER.warn("No se pudo inicializar el cache de cupos para el evento {}: {}",
                        evento.id(), e.getMessage());
            }
        }

        if (inicializados > 0) {
            LOGGER.info("Se inicializaron {} eventos en el cache de Redis.", inicializados);
        }
        if (omitidos > 0) {
            LOGGER.debug("Se omitieron {} eventos al inicializar el cache de Redis.", omitidos);
        }
    }

    private boolean esCacheable(Evento evento) {
        if (evento == null || evento.estado() == null || evento.estado().getTipoEstado() == null) {
            return false;
        }
        if (evento.cupoMaximo() == null || evento.cupoMaximo() <= 0) {
            return false;
        }
        TipoEstadoEvento tipoEstado = evento.estado().getTipoEstado();
        return ESTADOS_CACHEABLES.contains(tipoEstado);
    }
}
