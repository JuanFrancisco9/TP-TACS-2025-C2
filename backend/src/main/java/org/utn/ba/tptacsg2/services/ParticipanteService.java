package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;
import org.utn.ba.tptacsg2.repositories.db.InscripcionRepositoryDB;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipanteService {
    private final InscripcionRepositoryDB inscripcionRepository;

    public ParticipanteService(InscripcionRepositoryDB inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    public List<Inscripcion> getInscripcionesDeParticipante(String idParticipante) {
        return inscripcionRepository.findByParticipante_Id(idParticipante);
    }
}
