package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;
import org.utn.ba.tptacsg2.repositories.OrganizadorRepository;
import org.utn.ba.tptacsg2.repositories.ParticipanteRepository;

import java.util.List;

@Service
public class EventoService {
    private final EventoRepository eventoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ParticipanteRepository participanteRepository;
    private final OrganizadorRepository organizadorRepository;
    private final GeneradorIDService generadorIDService;

    @Autowired
    public EventoService(EventoRepository eventoRepository, InscripcionRepository inscripcionRepository, OrganizadorRepository organizadorRepository, GeneradorIDService generadorIDService, ParticipanteRepository participanteRepository) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.organizadorRepository = organizadorRepository;
        this.generadorIDService = generadorIDService;
        this.participanteRepository = participanteRepository;
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

    public Evento getEvento(String eventoId){
        return eventoRepository.getEvento(eventoId).orElseThrow(()-> new RuntimeException("Evento " + eventoId + " no encontrado"));
    }

    public List<Participante> getParticipantes(String eventoId){
        List<Inscripcion> inscripciones = inscripcionRepository.getInscripcionesAEvento(this.getEvento(eventoId));

        List<Participante> participantes = inscripciones.stream().filter(i -> i.estado().tipoEstado() == TipoEstadoInscripcion.ACEPTADA)
                .map(i->i.participante()).toList();

        return participantes;
    }
}