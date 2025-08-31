package org.utn.ba.tptacsg2.models.events;

import org.utn.ba.tptacsg2.models.actors.Organizador;

import java.time.LocalDateTime;

public record SolicitudEvento(
        String organizadorId,
        String titulo,
        String descripcion,
        LocalDateTime fecha,
        String horaInicio,
        Float duracion,
        Ubicacion ubicacion,
        Integer cupoMaximo,
        Integer cupoMinimo,
        Precio precio,
        Categoria categoria
) {}
