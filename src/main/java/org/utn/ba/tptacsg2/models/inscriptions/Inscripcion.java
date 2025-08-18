package org.utn.ba.tptacsg2.models.inscriptions;

import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Evento;

import java.time.LocalDateTime;

public class Inscripcion {
    private Participante participante;
    private LocalDateTime fechaRegistro;
    private EstadoInscripcion estado;
    private Evento evento;
}
