package org.utn.ba.tptacsg2.dtos.output;

import org.utn.ba.tptacsg2.dtos.EventoDTO;

import java.util.List;

public record ResultadoBusquedaEvento(
        List<EventoDTO> eventos,
        Integer siguientePagina,
        Integer totalElementos,
        Integer totalPaginas
) {
}
