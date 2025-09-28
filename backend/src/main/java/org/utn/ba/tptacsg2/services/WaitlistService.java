package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.exceptions.EventoNoEncontradoException;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.dtos.SolicitudInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EstadoInscripcionRepository;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.db.EstadoInscripcionRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.EventoRepositoryDB;

import java.time.LocalDateTime;

@Service
public class WaitlistService {

    private final GeneradorIDService generadorIDService;
    private final EventoRepositoryDB eventoRepository;
    private final EstadoInscripcionRepositoryDB estadoInscripcionRepository;

    @Autowired
    public WaitlistService(GeneradorIDService generadorIDService, EventoRepositoryDB eventoRepository, EstadoInscripcionRepositoryDB estadoInscripcionRepository) {
        this.generadorIDService = generadorIDService;
        this.eventoRepository = eventoRepository;
        this.estadoInscripcionRepository = estadoInscripcionRepository;
    }

    public Inscripcion inscribirAWaitlist(SolicitudInscripcion solicitudInscripcion) {
        Evento evento = eventoRepository.findById(solicitudInscripcion.evento_id())
                .orElseThrow(() -> new EventoNoEncontradoException("No se encontró el evento " + solicitudInscripcion.evento_id()));

        EstadoInscripcion estadoInscripcion = new EstadoInscripcion(this.generadorIDService.generarID(), TipoEstadoInscripcion.PENDIENTE, LocalDateTime.now());

        Inscripcion inscripcionPendiente =  new Inscripcion(
                generadorIDService.generarID(),
                solicitudInscripcion.participante(),
                LocalDateTime.now(),
                estadoInscripcion,
                evento
        );

        estadoInscripcion.setInscripcion(inscripcionPendiente);

        this.estadoInscripcionRepository.save(estadoInscripcion);

        return inscripcionPendiente;
    }
}
