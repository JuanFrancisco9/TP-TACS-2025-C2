package org.utn.ba.tptacsg2.models.events;

public record EstadoEvento(
     TipoEstadoEvento tipoEstado,
     LocalDateTime fechaCambio
){}
