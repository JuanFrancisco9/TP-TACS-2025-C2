package org.utn.ba.tptacsg2.models.events;

import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;

import java.time.LocalDateTime;
import java.util.List;

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
        TipoEstadoEvento estado,
        Categoria categoria,
        List<String> etiquetas
) {}
