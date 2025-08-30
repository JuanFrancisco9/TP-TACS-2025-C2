package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
import org.utn.ba.tptacsg2.models.events.TipoEstadoEvento;
import org.utn.ba.tptacsg2.services.EventoService;

@RestController
public class EventoController {
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping("/evento")
    public ResponseEntity<Evento> crearEvento(@RequestBody SolicitudEvento solicitudEvento) {
        Evento evento = eventoService.registrarEvento(solicitudEvento);

        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
    }

    @PutMapping("/evento/{id_evento}")
    public ResponseEntity<Evento> modificarEvento(@PathVariable ("id_evento") String idEvento , @RequestParam("estado") TipoEstadoEvento estado) {
        Evento evento = eventoService.cambiarEstado(idEvento, estado);
        return ResponseEntity.status(HttpStatus.OK).body(evento);
    }
}
