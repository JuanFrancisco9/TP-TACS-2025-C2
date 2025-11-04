package org.utn.ba.tptacsg2.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.dtos.EstadisticasUsoDTO;
import org.utn.ba.tptacsg2.enums.TipoEstadistica;
import org.utn.ba.tptacsg2.services.EstadisticasService;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/estadisticas")
@PreAuthorize("hasRole('ADMIN')")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @GetMapping()
    public ResponseEntity<EstadisticasUsoDTO> obtenerEstadisticasUso(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) Set<TipoEstadistica> estadisticas) {
        
        EstadisticasUsoDTO resultado = estadisticasService.obtenerEstadisticasUso(fechaDesde, fechaHasta, estadisticas);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/completas")
    public ResponseEntity<EstadisticasUsoDTO> obtenerTodasLasEstadisticas(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        EstadisticasUsoDTO estadisticas = estadisticasService.obtenerEstadisticasUso(fechaDesde, fechaHasta, null);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/eventos/cantidad")
    public ResponseEntity<Integer> obtenerCantidadEventos(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        Integer cantidad = estadisticasService.obtenerCantidadEventos(fechaDesde, fechaHasta);
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/eventos/activos")
    public ResponseEntity<Integer> obtenerCantidadEventosActivos(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        Integer cantidad = estadisticasService.obtenerCantidadEventosActivos(fechaDesde, fechaHasta);
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/inscripciones/totales")
    public ResponseEntity<Integer> obtenerCantidadInscripcionesTotales(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        Integer cantidad = estadisticasService.obtenerCantidadInscripcionesTotales(fechaDesde, fechaHasta);
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/inscripciones/confirmadas")
    public ResponseEntity<Integer> obtenerCantidadInscripcionesConfirmadas(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        Integer cantidad = estadisticasService.obtenerCantidadInscripcionesConfirmadas(fechaDesde, fechaHasta);
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/inscripciones/waitlist")
    public ResponseEntity<Integer> obtenerCantidadInscripcionesWaitlist(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        Integer cantidad = estadisticasService.obtenerCantidadInscripcionesWaitlist(fechaDesde, fechaHasta);
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/conversion/waitlist")
    public ResponseEntity<Double> obtenerTasaConversionWaitlist(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        Double tasa = estadisticasService.obtenerTasaConversionWaitlist(fechaDesde, fechaHasta);
        return ResponseEntity.ok(tasa);
    }

    @GetMapping("/eventos/mas-popular")
    public ResponseEntity<String> obtenerEventoMasPopular(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        String evento = estadisticasService.obtenerEventoMasPopular(fechaDesde, fechaHasta);
        return ResponseEntity.ok(evento);
    }

    @GetMapping("/inscripciones/promedio-por-evento")
    public ResponseEntity<Double> obtenerPromedioInscripcionesPorEvento(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        Double promedio = estadisticasService.obtenerPromedioInscripcionesPorEvento(fechaDesde, fechaHasta);
        return ResponseEntity.ok(promedio);
    }
}
