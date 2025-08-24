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

import java.time.LocalDateTime;

@Service
public class WaitlistService {

    private final GeneradorIDService generadorIDService;
    private final EventoRepository eventoRepository;

    @Autowired
    public WaitlistService(GeneradorIDService generadorIDService, EventoRepository eventoRepository) {
        this.generadorIDService = generadorIDService;
        this.eventoRepository = eventoRepository;
    }

    public Inscripcion inscribirAWaitlist(SolicitudInscripcion solicitudInscripcion) {
        Evento evento = eventoRepository.getEvento(solicitudInscripcion.evento_id())
                .orElseThrow(() -> new EventoNoEncontradoException("No se encontró el evento " + solicitudInscripcion.evento_id()));

        return new Inscripcion(
                generadorIDService.generarID(),
                solicitudInscripcion.participante(),
                LocalDateTime.now(),
                new EstadoInscripcion(TipoEstadoInscripcion.PENDIENTE,
                        LocalDateTime.now()),
                evento
        );
    }
}
