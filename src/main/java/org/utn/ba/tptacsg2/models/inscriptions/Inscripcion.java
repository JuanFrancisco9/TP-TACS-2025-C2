package org.utn.ba.tptacsg2.models.inscriptions;

import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Evento;

import java.time.LocalDateTime;

public record Inscripcion (
        String id,
        Participante participante,
        LocalDateTime fechaRegistro,
        EstadoInscripcion estado,
        Evento evento
){


}
