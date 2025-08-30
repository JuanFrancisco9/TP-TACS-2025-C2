package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
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
}
