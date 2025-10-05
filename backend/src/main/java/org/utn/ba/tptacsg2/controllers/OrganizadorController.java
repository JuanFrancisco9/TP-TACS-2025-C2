package org.utn.ba.tptacsg2.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.utn.ba.tptacsg2.dtos.EventoDTO;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.services.EventoService;
import org.utn.ba.tptacsg2.services.OrganizadorService;
import java.util.List;

@RestController
@RequestMapping("/organizadores")
public class OrganizadorController {

    private final OrganizadorService organizadorService;
    private final EventoService eventoService;

    @Autowired
    public OrganizadorController(OrganizadorService organizadorService, EventoService eventoService) {
        this.organizadorService = organizadorService;
        this.eventoService = eventoService;
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @GetMapping("/eventos/{id_organizador}")
    public ResponseEntity<List<EventoDTO>> getEventosDeOrganizadorPorUsuario(@PathVariable("id_organizador") String idOrganizador) {
        List<Evento> eventos = organizadorService.getEventosDeOrganizador(idOrganizador);
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<EventoDTO> eventosDTO = eventos.stream()
                .map(eventoService::mapearEventoDTO)
                .toList();
        return ResponseEntity.ok(eventosDTO);
    }

}
