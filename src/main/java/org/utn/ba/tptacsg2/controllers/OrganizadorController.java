package org.utn.ba.tptacsg2.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/eventos/{id_organizador}")
    public List<Evento> getEventosDeOrganizador(@PathVariable("id_organizador") String idOrganizador) {
        return organizadorService.getEventosDeOrganizador(idOrganizador);
    }
}

