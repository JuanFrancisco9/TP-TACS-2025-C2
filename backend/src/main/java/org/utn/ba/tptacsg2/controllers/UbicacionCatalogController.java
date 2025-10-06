package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.dtos.location.LocalidadDTO;
import org.utn.ba.tptacsg2.dtos.location.ProvinciaDTO;
import org.utn.ba.tptacsg2.services.UbicacionCatalogService;

import java.util.List;

@RestController
@RequestMapping("/ubicacion")
public class UbicacionCatalogController {

    private final UbicacionCatalogService ubicacionCatalogService;

    public UbicacionCatalogController(UbicacionCatalogService ubicacionCatalogService) {
        this.ubicacionCatalogService = ubicacionCatalogService;
    }

    @GetMapping("/provincias")
    public ResponseEntity<List<ProvinciaDTO>> getProvincias() {
        return ResponseEntity.ok(ubicacionCatalogService.listarProvincias());
    }

    @GetMapping("/provincias/{provinciaId}/localidades")
    public ResponseEntity<List<LocalidadDTO>> getLocalidades(@PathVariable String provinciaId) {
        return ResponseEntity.ok(ubicacionCatalogService.listarLocalidadesPorProvincia(provinciaId));
    }
}
