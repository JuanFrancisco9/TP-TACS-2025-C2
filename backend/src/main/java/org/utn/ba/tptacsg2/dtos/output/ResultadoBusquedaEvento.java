package org.utn.ba.tptacsg2.dtos.output;

import org.utn.ba.tptacsg2.models.events.Evento;

import java.util.List;

public record ResultadoBusquedaEvento(
        List<Evento> eventos,
        Integer siguientePagina,
        Integer totalElementos,
        Integer totalPaginas
) {
}
