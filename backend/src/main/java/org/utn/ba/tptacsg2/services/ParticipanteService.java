package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.InscripcionDTO;
import org.utn.ba.tptacsg2.dtos.EventoDTO;
import org.utn.ba.tptacsg2.dtos.ParticipanteDTO;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.repositories.db.InscripcionRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.ParticipanteRepositoryDB;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipanteService {
    private final InscripcionRepositoryDB inscripcionRepository;
    private final EventoService eventoService;
    private final ParticipanteRepositoryDB participanteRepository;

    public ParticipanteService(InscripcionRepositoryDB inscripcionRepository, EventoService eventoService, ParticipanteRepositoryDB participanteRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.eventoService = eventoService;
        this.participanteRepository = participanteRepository;
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

    public ParticipanteDTO getParticipanteById(String idParticipante) {
        return participanteRepository.findById(idParticipante)
                .map(p -> new ParticipanteDTO(
                        p.nombre(),
                        p.apellido(),
                        p.dni()
                ))
                .orElse(null);
    }
    public ParticipanteDTO actualizarParticipante(String idParticipante, ParticipanteDTO dto) {
        return participanteRepository.findById(idParticipante)
                .map(p -> {
                    Participante actualizado = new Participante(
                            p.id(),
                            dto.nombre(),
                            dto.apellido(),
                            dto.dni(),
                            p.usuario() // preserva la relación con el usuario
                    );
                    participanteRepository.save(actualizado);
                    return new ParticipanteDTO(actualizado.nombre(), actualizado.apellido(), actualizado.dni());
                })
                .orElse(null);
    }
}
