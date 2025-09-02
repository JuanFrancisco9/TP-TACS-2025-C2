package org.utn.ba.tptacsg2.dtos;

import org.utn.ba.tptacsg2.models.actors.Participante;

public record SolicitudInscripcion(
        Participante participante,
        String evento_id
) {
}
