package org.utn.ba.tptacsg2.models.events;


import java.time.LocalDateTime;

public record EstadoEvento(
     TipoEstadoEvento tipoEstado,
     LocalDateTime fechaCambio
){}
