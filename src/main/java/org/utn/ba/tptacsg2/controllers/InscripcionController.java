package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.SolicitudInscripcion;
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

}
