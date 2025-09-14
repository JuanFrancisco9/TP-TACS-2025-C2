package org.utn.ba.tptacsg2.dtos.output;

import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;

import java.util.List;

public record Waitlist (
        List<Inscripcion> inscripcionesSinConfirmar,
        Evento evento

) {

}