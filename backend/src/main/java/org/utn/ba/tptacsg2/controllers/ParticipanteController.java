package org.utn.ba.tptacsg2.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.utn.ba.tptacsg2.dtos.InscripcionDTO;
import org.utn.ba.tptacsg2.dtos.ParticipanteDTO;
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
    @PreAuthorize("hasAnyRole('USER', 'ORGANIZER')")
    @GetMapping("/{id_usuario}")
    public ResponseEntity<ParticipanteDTO> getUsuario(@PathVariable("id_usuario") String idUsuario) {
        ParticipanteDTO participante = participanteService.getParticipanteById(idUsuario);
        if (participante == null) {
            return ResponseEntity.noContent().build();
        }
        System.out.println(participante);
        return ResponseEntity.ok(participante);
    }

    @PreAuthorize("hasAnyRole('USER', 'ORGANIZER')")
    @PutMapping("/{id_participante}")
    public ResponseEntity<ParticipanteDTO> actualizarParticipante(
            @PathVariable("id_participante") String idParticipante,
            @RequestBody ParticipanteDTO participanteDTO) {

        ParticipanteDTO actualizado = participanteService.actualizarParticipante(idParticipante, participanteDTO);

        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(actualizado);
    }
}
