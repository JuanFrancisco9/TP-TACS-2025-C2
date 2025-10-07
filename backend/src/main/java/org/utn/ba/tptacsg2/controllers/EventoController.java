package org.utn.ba.tptacsg2.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.utn.ba.tptacsg2.dtos.EventoDTO;
import org.utn.ba.tptacsg2.dtos.FiltrosDTO;
import org.utn.ba.tptacsg2.dtos.ParticipanteDTO;
import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;
import org.utn.ba.tptacsg2.dtos.output.ResultadoBusquedaEvento;
import org.utn.ba.tptacsg2.exceptions.InscripcionNoEncontradaException;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
import org.utn.ba.tptacsg2.models.events.*;
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

    @PreAuthorize("hasRole('ORGANIZER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventoDTO> crearEvento(@RequestBody SolicitudEvento solicitudEvento) {
        EventoDTO eventoResponse = eventoService.registrarEventoConImagen(solicitudEvento, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoResponse);
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventoDTO> crearEventoConImagen(
            @RequestPart("evento") SolicitudEvento solicitudEvento,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        EventoDTO eventoResponse = eventoService.registrarEventoConImagen(solicitudEvento, imagen);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoResponse);
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @PatchMapping("/{id_evento}")
    public ResponseEntity<Evento> modificarEstadoEvento(@PathVariable ("id_evento") String idEvento , @RequestParam("estado") TipoEstadoEvento estado) {
        Evento evento = eventoService.cambiarEstado(idEvento, estado);
        return ResponseEntity.status(HttpStatus.OK).body(evento);
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @PutMapping("/{id_evento}")
    public ResponseEntity<Evento> modificarEvento(@PathVariable ("id_evento") String idEvento,  @RequestBody Evento eventoActualizado) {
        Evento evento = eventoService.actualizarEvento(idEvento, eventoActualizado);
        return ResponseEntity.status(HttpStatus.OK).body(evento);
    }

    @PreAuthorize("hasAnyRole('USER', 'ORGANIZER', 'ADMIN')")
    @GetMapping("/{id_evento}")
    public ResponseEntity<EventoDTO> obtenerEventoPorId(@PathVariable("id_evento") String idEvento) {
        EventoDTO evento = eventoService.obtenerEventoPorId(idEvento);
        return ResponseEntity.ok(evento);
    }

    /**
     * EP para obtener info de los participantes (nombre, apellido y dni) con inscripción ACEPTADA correspondientes a un evento
     * input: eventoId -> Strging id del evento por path param
     * output: 200 + lista con parcipantes,
     *         404 + mensaje de error, en caso de fallo
     */
    @PreAuthorize("hasRole('ORGANIZER')")
    @GetMapping("/{eventoId}/participantes")
    public ResponseEntity<List<ParticipanteDTO>> getParticipantesFromEvento(@PathVariable("eventoId") String eventoId){
        List<ParticipanteDTO> participantesDTO;
        try {
            participantesDTO = eventoService.getParticipantes(eventoId).stream().map(p -> new ParticipanteDTO(p.nombre(),p.apellido(),p.dni())).toList();
        } catch(RuntimeException e){
           throw new InscripcionNoEncontradaException("No se encontraron participantes para el evento",e);
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) String palabrasClave,
            @RequestParam(defaultValue = "1") Integer nroPagina){

        FiltrosDTO filtros = new FiltrosDTO(fechaInicio, fechaFin, categoria, ubicacion, precioMax, precioMin, palabrasClave, nroPagina);

        ResultadoBusquedaEvento resultado = eventoService.buscarEventos(filtros);

        return ResponseEntity.ok(resultado);
    }


}
