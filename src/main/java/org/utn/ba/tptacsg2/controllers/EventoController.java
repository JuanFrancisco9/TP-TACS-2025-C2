package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.models.events.Evento;

@RestController
public class EventoController {

    @PostMapping("/evento")
    public ResponseEntity<Evento> crearEvento(@RequestBody Evento evento) {
        //Guardar en mongo en el futuro
        return ResponseEntity.status(201).body(evento);
    }
}
