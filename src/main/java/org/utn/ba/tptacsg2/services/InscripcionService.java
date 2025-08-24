package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.output.Waitlist;
import org.utn.ba.tptacsg2.exceptions.EventoNoEncontradoException;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcionV2;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.dtos.SolicitudInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EstadoInscripcionRepository;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class InscripcionService {

    private final EventoRepository eventoRepository;
    private final EventoService eventoService;
    private final InscripcionRepository inscripcionRepository;
    private final WaitlistService waitlistService;
    private final GeneradorIDService generadorIDService;
    private final EstadoInscripcionRepository estadoInscripcionRepository;

    @Autowired
    public InscripcionService (EventoRepository eventoRepository, InscripcionRepository inscripcionRepository,
                               WaitlistService waitlistService, GeneradorIDService generadorIDService, EventoService eventoService, EstadoInscripcionRepository estadoInscripcionRepository) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.waitlistService = waitlistService;
        this.generadorIDService = generadorIDService;
        this.eventoService = eventoService;
        this.estadoInscripcionRepository = estadoInscripcionRepository;
    }

    public Inscripcion inscribir(SolicitudInscripcion solicitud) {
        Evento evento = eventoRepository.getEvento(solicitud.evento_id())
                        .orElseThrow(() -> new EventoNoEncontradoException("No se encontró el evento " + solicitud.evento_id()));

        if (eventoService.cuposDisponibles(evento) <= 0) {
            Inscripcion inscripcionPendiente = this.waitlistService.inscribirAWaitlist(solicitud);
            this.inscripcionRepository.guardarInscripcion(inscripcionPendiente);
            return inscripcionPendiente;
        }

        EstadoInscripcionV2 estadoInscripcionAceptada = new EstadoInscripcionV2(this.generadorIDService.generarID(), TipoEstadoInscripcion.ACEPTADA, LocalDateTime.now());

        Inscripcion inscripcionAceptada = new Inscripcion(
                generadorIDService.generarID(),
                solicitud.participante(),
                LocalDateTime.now(),
                estadoInscripcionAceptada,
                evento
        );

        estadoInscripcionAceptada.setInscripcion(inscripcionAceptada);
        //TODO chequear cómo se maneja esto en Mongo
        inscripcionRepository.guardarInscripcion(inscripcionAceptada);
        this.estadoInscripcionRepository.guardarEstadoInscripcion(estadoInscripcionAceptada);
        return inscripcionAceptada;
    }

    public Inscripcion cancelarInscripcion(Long inscripcionId) {

        // todo this.moverInscripcionAConfirmadaDelEvento();

        return null;
    }

    private void moverInscripcionAConfirmadaDelEvento(Evento evento) {

        Inscripcion inscripcionAConfirmar = this.inscripcionRepository.getPrimerInscripcionDeWaitlist(evento);

        EstadoInscripcionV2 nuevoEstado = new EstadoInscripcionV2(this.generadorIDService.generarID(), TipoEstadoInscripcion.ACEPTADA, inscripcionAConfirmar, LocalDateTime.now());

        Inscripcion inscripcionActualizada = new Inscripcion(
                inscripcionAConfirmar.id(),
                inscripcionAConfirmar.participante(),
                inscripcionAConfirmar.fechaRegistro(),
                nuevoEstado,
                inscripcionAConfirmar.evento()
        );

        this.estadoInscripcionRepository.guardarEstadoInscripcion(nuevoEstado);

        this.inscripcionRepository.actualizarInscripcion(inscripcionActualizada);

        //TODO notificacion de algun tipo?? Que haces ? -> Es un flujo medio "encubierto" -> no parte de un endpoint en si
    }

    public Waitlist getWaitlist(String eventoId) {

        Evento evento = this.eventoRepository.getEvento(eventoId)
                .orElseThrow(() -> new EventoNoEncontradoException("No se encontró el evento con ID: " + eventoId));

        return new Waitlist(inscripcionRepository.getWailist(evento), evento);
    }
}
