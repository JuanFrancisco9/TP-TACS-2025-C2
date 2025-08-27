package org.utn.ba.tptacsg2.models.inscriptions;


import java.time.LocalDateTime;

public record EstadoInscripcion (
        TipoEstadoInscripcion tipoEstado,
        LocalDateTime fechaCambio
){}
