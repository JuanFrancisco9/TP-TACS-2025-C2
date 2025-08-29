package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;
import org.utn.ba.tptacsg2.repositories.ParticipanteRepository;

import java.util.List;

@Service
public class EventoService {
    private final EventoRepository eventoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ParticipanteRepository participanteRepository;

    @Autowired
    public EventoService(EventoRepository eventoRepository, InscripcionRepository inscripcionRepository, ParticipanteRepository participanteRepository) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.participanteRepository = participanteRepository;
    }

    public Integer cuposDisponibles(Evento evento) {
        return evento.cupoMaximo() -  inscripcionRepository.getInscripcionesAEvento(evento)
                .stream().filter(inscripcion -> inscripcion.estado().tipoEstado().equals(TipoEstadoInscripcion.ACEPTADA))
                .toList().size();
    }

    public Evento getEvento(String eventoId){
        return eventoRepository.getEvento(eventoId).orElseThrow(()-> new RuntimeException("Evento " + eventoId + " no encontrado"));
    }

    public List<Participante> getParticipantes(String eventoId){
        List<Inscripcion> inscripciones = inscripcionRepository.getInscripcionesAEvento(this.getEvento(eventoId));

        List<Participante> participantes = inscripciones.stream().map(i->i.participante()).toList();

        return participantes;
    }

    public List<Evento> getAll(){
        return eventoRepository.getEventos();
    }

    public void guardarEvento(Evento evento){
        eventoRepository.guardarEvento(evento);
    }
}
