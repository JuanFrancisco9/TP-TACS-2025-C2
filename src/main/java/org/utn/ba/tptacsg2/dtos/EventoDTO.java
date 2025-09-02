package org.utn.ba.tptacsg2.dtos;

import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.Precio;
import org.utn.ba.tptacsg2.models.events.Ubicacion;

import java.time.LocalDateTime;

public record EventoDTO(
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
){}
