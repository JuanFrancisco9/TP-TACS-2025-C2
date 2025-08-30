package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.utn.ba.tptacsg2.dtos.FiltrosDTO;
import org.utn.ba.tptacsg2.dtos.output.ResultadoBusquedaEvento;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
import org.utn.ba.tptacsg2.services.EventoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController("/eventos")
public class EventoController {
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping()
    public ResponseEntity<Evento> crearEvento(@RequestBody SolicitudEvento solicitudEvento) {
        Evento evento = eventoService.registrarEvento(solicitudEvento);

        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
    }

    @GetMapping()
    public ResponseEntity<ResultadoBusquedaEvento> buscarEventos(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam() String palabrasClave,
            @RequestParam(defaultValue = "1") Integer nroPagina){

        FiltrosDTO filtros = new FiltrosDTO(fechaInicio, fechaFin, categoria, precioMin, precioMax, palabrasClave, nroPagina);

        ResultadoBusquedaEvento resultado = eventoService.buscarEventos(filtros);
        return ResponseEntity.ok(resultado);
    }


}
