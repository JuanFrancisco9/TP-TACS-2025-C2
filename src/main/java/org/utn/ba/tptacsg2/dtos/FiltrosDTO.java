package org.utn.ba.tptacsg2.dtos;

import java.time.LocalDate;

public record FiltrosDTO(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        String categoria,
        Double precioMaximo,
        Double precioMinimo,
        String palabrasClave,
        Integer nroPagina
) {}