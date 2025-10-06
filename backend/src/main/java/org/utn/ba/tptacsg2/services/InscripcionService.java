package org.utn.ba.tptacsg2.services;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.output.Waitlist;
import org.utn.ba.tptacsg2.exceptions.EventoNoEncontradoException;
import org.utn.ba.tptacsg2.exceptions.EventoSinConfirmarException;
import org.utn.ba.tptacsg2.exceptions.InscripcionNoEncontradaException;
import org.utn.ba.tptacsg2.exceptions.InscripcionDuplicadaException;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.dtos.SolicitudInscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.db.EstadoInscripcionRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.EventoRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.InscripcionRepositoryDB;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class InscripcionService {

    private final EventoRepositoryDB eventoRepository;
    private final EventoService eventoService;
    private final InscripcionRepositoryDB inscripcionRepository;
    private final WaitlistService waitlistService;
    private final GeneradorIDService generadorIDService;
    private final EstadoInscripcionRepositoryDB estadoInscripcionRepository;
    private final EventoLockService eventoLockService;

    @Autowired
    public InscripcionService (EventoRepositoryDB eventoRepository, InscripcionRepositoryDB inscripcionRepository,
                               WaitlistService waitlistService, GeneradorIDService generadorIDService,
                               EventoService eventoService, EstadoInscripcionRepositoryDB estadoInscripcionRepository,
                               EventoLockService eventoLockService) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.waitlistService = waitlistService;
        this.generadorIDService = generadorIDService;
        this.eventoService = eventoService;
        this.estadoInscripcionRepository = estadoInscripcionRepository;
        this.eventoLockService = eventoLockService;
    }

    public Inscripcion inscribir(SolicitudInscripcion solicitud) {
        Evento evento = eventoRepository.findById(solicitud.evento_id())
                        .orElseThrow(() -> new EventoNoEncontradoException("No se encontró el evento " + solicitud.evento_id()));

        if(evento.estado().getTipoEstado() == TipoEstadoEvento.CONFIRMADO) {
            ReentrantLock lock = eventoLockService.getLock(evento.id());
            try {
                lock.lock();
                this.validarParticipanteNoInscripto(solicitud, evento);
                if (eventoService.cuposDisponibles(evento) <= 0) {
                    Inscripcion inscripcionPendiente = this.waitlistService.inscribirAWaitlist(solicitud);
                    this.inscripcionRepository.save(inscripcionPendiente);
                    return inscripcionPendiente;
                }

                EstadoInscripcion estadoInscripcionAceptada = new EstadoInscripcion(this.generadorIDService.generarID(), TipoEstadoInscripcion.ACEPTADA, LocalDateTime.now());

                Inscripcion inscripcionAceptada = new Inscripcion(
                        generadorIDService.generarID(),
                        solicitud.participante(),
                        LocalDateTime.now(),
                        estadoInscripcionAceptada,
                        evento
                );

                estadoInscripcionAceptada.setInscripcion(inscripcionAceptada);
                //TODO chequear cómo se maneja esto en Mongo

                inscripcionRepository.save(inscripcionAceptada);
                this.estadoInscripcionRepository.save(estadoInscripcionAceptada);
                return inscripcionAceptada;

            } finally {
                lock.unlock();
            }
        }

        else {
            throw new EventoSinConfirmarException("No se puede inscribir a un evento que no está confirmado. Estado actual: " + evento.estado().getTipoEstado());
        }

    }

    public Inscripcion cancelarInscripcion(String inscripcionId) {

        Optional<Inscripcion> inscripcionACancelar = this.inscripcionRepository.findById(inscripcionId);

        if(inscripcionACancelar.isEmpty()) {
            throw new InscripcionNoEncontradaException("No se encontró la inscripcion " + inscripcionId);
        }

        Inscripcion inscripcion = inscripcionACancelar.get();

        EstadoInscripcion estadoCancelado = new EstadoInscripcion(
                generadorIDService.generarID(),
                TipoEstadoInscripcion.CANCELADA,
                inscripcion,
                LocalDateTime.now()
        );

        Inscripcion inscripcionCancelada = new Inscripcion(
                inscripcion.id(),
                inscripcion.participante(),
                inscripcion.fechaRegistro(),
                estadoCancelado,
                inscripcion.evento()
        );

        this.estadoInscripcionRepository.save(estadoCancelado);
        this.inscripcionRepository.save(inscripcionCancelada);

        this.moverInscripcionEnWaitlistAConfirmada(inscripcion.evento());

        return inscripcionCancelada;
    }

    private void validarParticipanteNoInscripto(SolicitudInscripcion solicitud, Evento evento) {
        if (solicitud.participante() == null || solicitud.participante().id() == null) {
            return;
        }
        List<Inscripcion> inscripcionesDelParticipante = Optional.ofNullable(this.inscripcionRepository.findByParticipante_Id(solicitud.participante().id()))
                .orElseGet(Collections::emptyList);

        boolean yaInscripto = inscripcionesDelParticipante.stream()
                .filter(inscripcion -> inscripcion.evento() != null && evento.id().equals(inscripcion.evento().id()))
                .anyMatch(inscripcion -> inscripcion.estado() != null && inscripcion.estado().getTipoEstado() != TipoEstadoInscripcion.CANCELADA);
        if (yaInscripto) {
            String participanteDescripcion = Optional.ofNullable(solicitud.participante())
                    .map(participante -> {
                        if (participante.usuario() != null && participante.usuario().username() != null && !participante.usuario().username().isBlank()) {
                            return participante.usuario().username();
                        }
                        String nombre = participante.nombre() != null ? participante.nombre().trim() : "";
                        String apellido = participante.apellido() != null ? participante.apellido().trim() : "";
                        String nombreCompleto = (nombre + " " + apellido).trim();
                        if (!nombreCompleto.isBlank()) {
                            return nombreCompleto;
                        }
                        return Optional.ofNullable(participante.id()).orElse("desconocido");
                    })
                    .orElse("desconocido");

            String eventoDescripcion = Optional.ofNullable(evento.titulo())
                    .filter(titulo -> !titulo.isBlank())
                    .orElse(Optional.ofNullable(evento.id()).orElse("desconocido"));

            throw new InscripcionDuplicadaException("El participante " + participanteDescripcion + " ya se encuentra inscripto en el evento " + eventoDescripcion);
        }
    }

    private void moverInscripcionEnWaitlistAConfirmada(Evento evento) {

        Optional<Inscripcion> inscripcionAConfirmar = this.inscripcionRepository.findFirstInWaitlistByEventoAndTipoEstado(evento.id(), TipoEstadoInscripcion.PENDIENTE);

        if (inscripcionAConfirmar.isEmpty()) {
            return;
        }

        EstadoInscripcion nuevoEstado = new EstadoInscripcion(this.generadorIDService.generarID(), TipoEstadoInscripcion.ACEPTADA, inscripcionAConfirmar.get(), LocalDateTime.now());

        Inscripcion inscripcionActualizada = new Inscripcion(
                inscripcionAConfirmar.get().id(),
                inscripcionAConfirmar.get().participante(),
                inscripcionAConfirmar.get().fechaRegistro(),
                nuevoEstado,
                inscripcionAConfirmar.get().evento()
        );

        this.estadoInscripcionRepository.save(nuevoEstado);

        this.inscripcionRepository.save(inscripcionActualizada);

    }

    public Waitlist getWaitlist(String eventoId) {

        Evento evento = this.eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventoNoEncontradoException("No se encontró el evento con ID: " + eventoId));

        return new Waitlist(inscripcionRepository.findWaitlistByEventoOrderByFechaAsc(evento.id(), TipoEstadoInscripcion.PENDIENTE), evento);
    }

    /*
    public ResponseEntity<List<Inscripcion>> getAll() {
        return ResponseEntity.ok(inscripcionRepository.findAll());
    }
    */
}
