package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.ba.tptacsg2.dtos.output.Waitlist;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.dtos.SolicitudInscripcion;
import org.utn.ba.tptacsg2.services.InscripcionService;

@RestController
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }


    @PostMapping("/inscripcion")
    public ResponseEntity<Inscripcion> inscribirse(@RequestBody SolicitudInscripcion solicitudInscripcion) {
        return ResponseEntity.ok(this.inscripcionService.inscribir(solicitudInscripcion));
    }

    @GetMapping("/waitlist/{eventoId}")
    public ResponseEntity<Waitlist> getWaitlist(@PathVariable String eventoId) {
        return ResponseEntity.ok(this.inscripcionService.getWaitlist(eventoId));
    }

}

