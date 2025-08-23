package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;

@Service
public class EventoService {
    private final EventoRepository eventoRepository;
    private final InscripcionRepository inscripcionRepository;

    @Autowired
    public EventoService(EventoRepository eventoRepository,  InscripcionRepository inscripcionRepository) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    public Integer cuposDisponibles(Evento evento) {
        return evento.cupoMaximo() -  inscripcionRepository.getInscripcionesAEvento(evento)
                .stream().filter(inscripcion -> inscripcion.estado().tipoEstado().equals(TipoEstadoInscripcion.ACEPTADA))
                .toList().size();
    }
}
