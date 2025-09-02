package org.utn.ba.tptacsg2.models.events;

import org.utn.ba.tptacsg2.models.actors.Organizador;

import java.time.LocalDateTime;
import java.util.List;


public record Evento (
     String id,
     String titulo,
     String descripcion,
     LocalDateTime fecha,
     String horaInicio,
     Float duracion,
     Ubicacion ubicacion,
     Integer cupoMaximo,
     Integer cupoMinimo,
     Precio precio,
     Organizador organizador,
     EstadoEvento estado,
     Categoria categoria,
     List<String> etiquetas
) {}
