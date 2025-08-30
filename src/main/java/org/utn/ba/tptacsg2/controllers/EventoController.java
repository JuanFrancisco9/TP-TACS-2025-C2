package org.utn.ba.tptacsg2.controllers;

import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.utn.ba.tptacsg2.dtos.ParticipanteDTO;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
import org.utn.ba.tptacsg2.services.EventoService;
import org.utn.ba.tptacsg2.services.EventoService;

import java.util.List;

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

    /**
     * EP para obtener info de los participantes (nombre, apellido y dni) con inscripción ACEPTADA correspondientes a un evento
     * input: eventoId -> Strging id del evento por path param
     * output: 200 + lista con parcipantes,
     *         404 + mensaje de error, en caso de fallo
     */

    @GetMapping("/evento/{eventoId}/participantes")
    public ResponseEntity<?> getParticipantesFromEvento(@PathVariable("eventoId") String eventoId){
        List<ParticipanteDTO> participantesDTO;
        try {
            participantesDTO = eventoService.getParticipantes(eventoId).stream().map(p -> new ParticipanteDTO(p.nombre(),p.apellido(),p.dni())).toList();
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(participantesDTO);
    }
}
