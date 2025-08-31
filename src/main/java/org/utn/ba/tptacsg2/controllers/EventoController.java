package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.utn.ba.tptacsg2.dtos.FiltrosDTO;
import org.utn.ba.tptacsg2.dtos.ParticipanteDTO;
import org.utn.ba.tptacsg2.dtos.output.ResultadoBusquedaEvento;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
import org.utn.ba.tptacsg2.models.events.TipoEstadoEvento;
import org.utn.ba.tptacsg2.services.EventoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping()
    public ResponseEntity<Evento> crearEvento(@RequestBody SolicitudEvento solicitudEvento) {
        Evento evento = eventoService.registrarEvento(solicitudEvento);

        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
    }

    @PutMapping("/{id_evento}")
    public ResponseEntity<Evento> modificarEvento(@PathVariable ("id_evento") String idEvento , @RequestParam("estado") TipoEstadoEvento estado) {
        Evento evento = eventoService.cambiarEstado(idEvento, estado);
        return ResponseEntity.status(HttpStatus.OK).body(evento);
    }

    /**
     * EP para obtener info de los participantes (nombre, apellido y dni) con inscripción ACEPTADA correspondientes a un evento
     * input: eventoId -> Strging id del evento por path param
     * output: 200 + lista con parcipantes,
     *         404 + mensaje de error, en caso de fallo
     */

    @GetMapping("/{eventoId}/participantes")
    public ResponseEntity<?> getParticipantesFromEvento(@PathVariable("eventoId") String eventoId){
        List<ParticipanteDTO> participantesDTO;
        try {
            participantesDTO = eventoService.getParticipantes(eventoId).stream().map(p -> new ParticipanteDTO(p.nombre(),p.apellido(),p.dni())).toList();
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(participantesDTO);
    }


    /**
     * EP para obtener eventos filtrados en base a yn rango de fechas, categoria, ubicacion, rango de precios y palabras clave
     * de forma paginada.
     * input: Query Params
     * output: 200 + lista con eventos, total de eventos y total de paginas,
     *         404 + mensaje de error, en caso de fallo
     */
    @GetMapping()
    public ResponseEntity<ResultadoBusquedaEvento> buscarEventos(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam() String palabrasClave,
            @RequestParam(defaultValue = "1") Integer nroPagina){

        FiltrosDTO filtros = new FiltrosDTO(fechaInicio, fechaFin, categoria, precioMin, precioMax, palabrasClave, nroPagina);

        ResultadoBusquedaEvento resultado = eventoService.buscarEventos(filtros);

        return ResponseEntity.ok(resultado);
    }


}
