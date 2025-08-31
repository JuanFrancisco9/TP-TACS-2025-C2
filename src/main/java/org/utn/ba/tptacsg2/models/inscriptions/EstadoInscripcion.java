package org.utn.ba.tptacsg2.models.inscriptions;


import java.time.LocalDateTime;

public record EstadoInscripcion (
        String id,
        TipoEstadoInscripcion tipoEstado,
        Inscripcion inscripcion,
        LocalDateTime fechaCambio
){}
