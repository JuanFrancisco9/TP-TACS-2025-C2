package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.dtos.EstadisticasUso;
import org.utn.ba.tptacsg2.services.EstadisticasService;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @GetMapping("/uso")
    public ResponseEntity<EstadisticasUso> obtenerEstadisticasUso() {
        EstadisticasUso estadisticas = estadisticasService.obtenerEstadisticasUso();
        return ResponseEntity.ok(estadisticas);
    }
}
