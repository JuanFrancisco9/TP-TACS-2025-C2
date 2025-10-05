package org.utn.ba.tptacsg2.dtos;

import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.inscriptions.EstadoInscripcion;

import java.time.LocalDateTime;

public record InscripcionDTO(
        String id,
        Participante participante,
        LocalDateTime fechaRegistro,
        EstadoInscripcion estado,
        EventoDTO evento
) {}
