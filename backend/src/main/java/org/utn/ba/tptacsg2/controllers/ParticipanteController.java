package org.utn.ba.tptacsg2.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.utn.ba.tptacsg2.dtos.InscripcionDTO;
import org.utn.ba.tptacsg2.services.ParticipanteService;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/participantes")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    @Autowired
    public ParticipanteController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/inscripciones/{id_usuario}")
    public ResponseEntity<List<InscripcionDTO>> getInscripcionesDeUsuario(@PathVariable("id_usuario") String idUsuario) {
        List<InscripcionDTO> inscripciones = participanteService.getInscripcionesDeParticipante(idUsuario);
        if (inscripciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(inscripciones);
    }
}
