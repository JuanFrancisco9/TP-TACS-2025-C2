package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipanteService {
    private final InscripcionRepository inscripcionRepository;

    public ParticipanteService(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    public List<Evento> getEventosDeParticipante(String idParticipante) {
        List<Inscripcion> inscripciones = inscripcionRepository.getInscripcionesDeParticipante(idParticipante);
        return inscripciones.stream()
                .map(Inscripcion::evento)
                .collect(Collectors.toList());
    }

    public List<Inscripcion> getInscripcionesDeParticipante(String idParticipante) {
        return inscripcionRepository.getInscripcionesDeParticipante(idParticipante);
    }
}
