package org.utn.ba.tptacsg2.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.services.OrganizadorService;
import java.util.List;

@RestController
@RequestMapping("/organizadores")
public class OrganizadorController {

    private final OrganizadorService organizadorService;

    @Autowired
    public OrganizadorController(OrganizadorService organizadorService) {
        this.organizadorService = organizadorService;
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @GetMapping("/eventos/{id_usuario}")
    public ResponseEntity<List<Evento>> getEventosDeOrganizadorPorUsuario(@PathVariable("id_usuario") String idUsuario) {
        List<Evento> eventos = organizadorService.getEventosDeOrganizadorPorUsuario(idUsuario);
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventos);
    }

}
