package org.utn.ba.tptacsg2.controllers;

import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.dtos.ParticipanteDTO;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.services.EventoService;

import java.util.List;

@RestController
public class EventoController {

    private final Logger logger = LoggerFactory.getLogger(EventoService.class);
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping("/evento")
    public ResponseEntity<Evento> crearEvento(@RequestBody Evento evento) {
        eventoService.guardarEvento(evento);
        return ResponseEntity.status(201).body(evento);
    }

    @GetMapping("/evento/participantes")
    public ResponseEntity<?> getParticipantesFromEvento(@RequestBody String eventoId){
        List<ParticipanteDTO> participantesDTO;
        try {
            participantesDTO = eventoService.getParticipantes(eventoId).stream().map(p -> new ParticipanteDTO(p.nombre(),p.apellido(),p.dni())).toList();
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(200).body(participantesDTO);
    }

    @GetMapping("/test/eventos")
    public ResponseEntity<List<Evento>> getEventos(){
        return ResponseEntity.status(200).body(eventoService.getAll());
    }

}
