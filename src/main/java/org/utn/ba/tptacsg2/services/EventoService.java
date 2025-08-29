package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
import org.utn.ba.tptacsg2.models.events.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;
import org.utn.ba.tptacsg2.repositories.OrganizadorRepository;

import java.time.LocalDateTime;

@Service
public class EventoService {
    private final EventoRepository eventoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final OrganizadorRepository organizadorRepository;
    private final GeneradorIDService generadorIDService;

    @Autowired
    public EventoService(EventoRepository eventoRepository, InscripcionRepository inscripcionRepository, OrganizadorRepository organizadorRepository, GeneradorIDService generadorIDService) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.organizadorRepository = organizadorRepository;
        this.generadorIDService = generadorIDService;
    }

    public Integer cuposDisponibles(Evento evento) {
        return evento.cupoMaximo() - inscripcionRepository.getInscripcionesAEvento(evento)
                .stream().filter(inscripcion -> inscripcion.estado().tipoEstado().equals(TipoEstadoInscripcion.ACEPTADA))
                .toList().size();
    }

    public Evento registrarEvento(SolicitudEvento solicitud) {
        Organizador organizador = organizadorRepository.getOrganizador(solicitud.organizadorId())
                .orElseThrow(() -> new RuntimeException("Organizador no encontrado"));

        Evento evento = new Evento(
                generadorIDService.generarID(),
                solicitud.evento().titulo(),
                solicitud.evento().descripcion(),
                solicitud.evento().fecha(),
                solicitud.evento().horaInicio(),
                solicitud.evento().duracion(),
                solicitud.evento().ubicacion(),
                solicitud.evento().cupoMaximo(),
                solicitud.evento().precio(),
                organizador,
                solicitud.evento().estado()
        );

        eventoRepository.guardarEvento(evento);

        return evento;
    }

    public Evento cambiarEstado(String idEvento,TipoEstadoEvento estado) {
        Evento evento = eventoRepository.getEvento(idEvento).orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        EstadoEvento estadoEvento = new EstadoEvento(estado, LocalDateTime.now());

        Evento eventoActualizado = new Evento(
                evento.id(),
                evento.titulo(),
                evento.descripcion(),
                evento.fecha(),
                evento.horaInicio(),
                evento.duracion(),
                evento.ubicacion(),
                evento.cupoMaximo(),
                evento.precio(),
                evento.organizador(),
                estadoEvento
        );

        eventoRepository.guardarEvento(eventoActualizado);

        return eventoActualizado;
    }
}