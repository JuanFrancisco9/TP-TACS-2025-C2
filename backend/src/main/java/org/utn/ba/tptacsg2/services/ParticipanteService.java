package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.InscripcionDTO;
import org.utn.ba.tptacsg2.dtos.EventoDTO;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.repositories.db.InscripcionRepositoryDB;

import java.util.List;

@Service
public class ParticipanteService {
    private final InscripcionRepositoryDB inscripcionRepository;
    private final EventoService eventoService;

    public ParticipanteService(InscripcionRepositoryDB inscripcionRepository, EventoService eventoService) {
        this.inscripcionRepository = inscripcionRepository;
        this.eventoService = eventoService;
    }

    public List<InscripcionDTO> getInscripcionesDeParticipante(String idParticipante) {
        return inscripcionRepository.findByParticipante_Id(idParticipante)
                .stream()
                .map(this::mapearInscripcion)
                .toList();
    }

    private InscripcionDTO mapearInscripcion(Inscripcion inscripcion) {
        EventoDTO eventoDTO = eventoService.mapearEventoDTO(inscripcion.evento());
        return new InscripcionDTO(
                inscripcion.id(),
                inscripcion.participante(),
                inscripcion.fechaRegistro(),
                inscripcion.estado(),
                eventoDTO
        );
    }
}
