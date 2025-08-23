package org.utn.ba.tptacsg2.models.events;

import org.utn.ba.tptacsg2.models.actors.Organizador;

import java.time.LocalDateTime;

public record Evento (
     String id,
     String titulo,
     String descripcion,
     LocalDateTime fecha,
     String horaInicio,
     Float duracion,
     Ubicacion ubicacion,
     Integer cupoMaximo,
     Precio precio,
     Organizador organizador,
     EstadoEvento estado
) {}
