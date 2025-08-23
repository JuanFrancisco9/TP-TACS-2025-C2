package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.exceptions.EventoNoEncontradoException;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.SolicitudInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;

import java.time.LocalDateTime;

@Service
public class InscripcionService {

    private final EventoRepository eventoRepository;
    private final EventoService eventoService;
    private final InscripcionRepository inscripcionRepository;
    private final WaitlistService waitlistService;
    private final GeneradorIDService generadorIDService;

    @Autowired
    public InscripcionService (EventoRepository eventoRepository, InscripcionRepository inscripcionRepository,
                               WaitlistService waitlistService, GeneradorIDService generadorIDService, EventoService eventoService) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.waitlistService = waitlistService;
        this.generadorIDService = generadorIDService;
        this.eventoService = eventoService;
    }

    public Inscripcion inscribir(SolicitudInscripcion solicitud) {
        Evento evento = eventoRepository.getEvento(solicitud.evento_id())
                        .orElseThrow(() -> new EventoNoEncontradoException("No se encontró el evento " + solicitud.evento_id()));

        if (eventoService.cuposDisponibles(evento) <= 0) {
            return this.waitlistService.inscribirAWaitlist(solicitud);
        }

        Inscripcion inscripcionAceptada = new Inscripcion(
                generadorIDService.generarID(),
                solicitud.participante(),
                LocalDateTime.now(),
                new EstadoInscripcion(TipoEstadoInscripcion.ACEPTADA,
                        LocalDateTime.now()),
                evento
        );
        inscripcionRepository.guardarInscripcion(inscripcionAceptada);

        return inscripcionAceptada;
    }
}
