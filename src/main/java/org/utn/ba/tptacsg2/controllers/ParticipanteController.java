package org.utn.ba.tptacsg2.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.services.ParticipanteService;
import java.util.List;

@RestController
@RequestMapping("/participantes")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    @Autowired
    public ParticipanteController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    @GetMapping("/inscripciones/{id_usuario}")
    public List<Inscripcion> getInscripcionesDeUsuario(@PathVariable("id_usuario") String idUsuario) {
        return participanteService.getInscripcionesDeParticipante(idUsuario);
    }
}
