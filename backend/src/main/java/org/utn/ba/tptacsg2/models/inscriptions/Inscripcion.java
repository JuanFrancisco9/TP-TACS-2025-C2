package org.utn.ba.tptacsg2.models.inscriptions;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Evento;

import java.time.LocalDateTime;
@Document(collation = "inscripciones")
public record Inscripcion (
        @Id
        String id,
        @DBRef
        Participante participante,
        LocalDateTime fechaRegistro,
        @DBRef
        EstadoInscripcion estado,
        @DBRef
        Evento evento
){


}
