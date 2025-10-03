package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.EstadisticasUsoDTO;
import org.utn.ba.tptacsg2.enums.TipoEstadistica;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;
import org.utn.ba.tptacsg2.repositories.db.EventoRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.InscripcionRepositoryDB;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {

    private final EventoRepositoryDB eventoRepository;
    private final InscripcionRepositoryDB inscripcionRepository;

    public EstadisticasService(EventoRepositoryDB eventoRepository, InscripcionRepositoryDB inscripcionRepository) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
    }


    public EstadisticasUsoDTO obtenerEstadisticasUso() {
        return obtenerEstadisticasUso(null, null, null);
    }

    public EstadisticasUsoDTO obtenerEstadisticasUso(LocalDate fechaDesde, LocalDate fechaHasta, Set<TipoEstadistica> estadisticasSolicitadas) {
        Integer cantidadEventos = null;
        Integer cantidadEventosActivos = null;
        Integer cantidadInscripcionesTotales = null;
        Integer cantidadInscripcionesConfirmadas = null;
        Integer cantidadInscripcionesWaitlist = null;
        Double tasaConversionWaitlist = null;
        String eventoMasPopular = null;
        Double promedioInscripcionesPorEvento = null;

        if (debeCalcular(estadisticasSolicitadas, TipoEstadistica.CANTIDAD_EVENTOS)) {
            cantidadEventos = obtenerCantidadEventos(fechaDesde, fechaHasta);
        }
        if (debeCalcular(estadisticasSolicitadas, TipoEstadistica.CANTIDAD_EVENTOS_ACTIVOS)) {
            cantidadEventosActivos = obtenerCantidadEventosActivos(fechaDesde, fechaHasta);
        }
        if (debeCalcular(estadisticasSolicitadas, TipoEstadistica.CANTIDAD_INSCRIPCIONES_TOTALES)) {
            cantidadInscripcionesTotales = obtenerCantidadInscripcionesTotales(fechaDesde, fechaHasta);
        }
        if (debeCalcular(estadisticasSolicitadas, TipoEstadistica.CANTIDAD_INSCRIPCIONES_CONFIRMADAS)) {
            cantidadInscripcionesConfirmadas = obtenerCantidadInscripcionesConfirmadas(fechaDesde, fechaHasta);
        }
        if (debeCalcular(estadisticasSolicitadas, TipoEstadistica.CANTIDAD_INSCRIPCIONES_WAITLIST)) {
            cantidadInscripcionesWaitlist = obtenerCantidadInscripcionesWaitlist(fechaDesde, fechaHasta);
        }
        if (debeCalcular(estadisticasSolicitadas, TipoEstadistica.TASA_CONVERSION_WAITLIST)) {
            tasaConversionWaitlist = obtenerTasaConversionWaitlist(fechaDesde, fechaHasta);
        }
        if (debeCalcular(estadisticasSolicitadas, TipoEstadistica.EVENTO_MAS_POPULAR)) {
            eventoMasPopular = obtenerEventoMasPopular(fechaDesde, fechaHasta);
        }
        if (debeCalcular(estadisticasSolicitadas, TipoEstadistica.PROMEDIO_INSCRIPCIONES_POR_EVENTO)) {
            promedioInscripcionesPorEvento = obtenerPromedioInscripcionesPorEvento(fechaDesde, fechaHasta);
        }

        return new EstadisticasUsoDTO(
            cantidadEventos,
            cantidadEventosActivos,
            cantidadInscripcionesTotales,
            cantidadInscripcionesConfirmadas,
            cantidadInscripcionesWaitlist,
            tasaConversionWaitlist,
            eventoMasPopular,
            promedioInscripcionesPorEvento
        );
    }

    private boolean debeCalcular(Set<TipoEstadistica> solicitadas, TipoEstadistica tipo) {
        return solicitadas == null || solicitadas.contains(tipo);
    }

    public Integer obtenerCantidadEventos(LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Evento> eventos = eventoRepository.findAll();
        return filtrarEventosPorFecha(eventos, fechaDesde, fechaHasta).size();
    }


    public Integer obtenerCantidadEventosActivos(LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Evento> eventos = eventoRepository.findAll();
        return filtrarEventosPorFecha(eventos, fechaDesde, fechaHasta).stream()
                .filter(evento -> evento.estado().getTipoEstado() == TipoEstadoEvento.CONFIRMADO)
                .toList().size();
    }

    public Integer obtenerCantidadInscripcionesTotales(LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Inscripcion> inscripciones = inscripcionRepository.findAll();
        return filtrarInscripcionesPorFecha(inscripciones, fechaDesde, fechaHasta).size();
    }

    public Integer obtenerCantidadInscripcionesConfirmadas(LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Inscripcion> inscripciones = inscripcionRepository.findAll();
        return filtrarInscripcionesPorFecha(inscripciones, fechaDesde, fechaHasta).stream()
                .filter(inscripcion -> inscripcion.estado().getTipoEstado() == TipoEstadoInscripcion.ACEPTADA)
                .toList().size();
    }

    public Integer obtenerCantidadInscripcionesWaitlist(LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Inscripcion> inscripciones = inscripcionRepository.findAll();
        return filtrarInscripcionesPorFecha(inscripciones, fechaDesde, fechaHasta).stream()
                .filter(inscripcion -> inscripcion.estado().getTipoEstado() == TipoEstadoInscripcion.PENDIENTE)
                .toList().size();
    }

    public Double obtenerTasaConversionWaitlist(LocalDate fechaDesde, LocalDate fechaHasta) {
        Integer totalInscripciones = obtenerCantidadInscripcionesTotales(fechaDesde, fechaHasta);
        Integer inscripcionesConfirmadas = obtenerCantidadInscripcionesConfirmadas(fechaDesde, fechaHasta);
        return calcularTasaConversion(totalInscripciones, inscripcionesConfirmadas);
    }

    public String obtenerEventoMasPopular(LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Evento> eventos = eventoRepository.findAll();
        List<Inscripcion> inscripciones = inscripcionRepository.findAll();
        
        List<Evento> eventosFiltrados = filtrarEventosPorFecha(eventos, fechaDesde, fechaHasta);
        List<Inscripcion> inscripcionesFiltradas = filtrarInscripcionesPorFecha(inscripciones, fechaDesde, fechaHasta);
        
        return encontrarEventoMasPopular(eventosFiltrados, inscripcionesFiltradas);
    }

    public Double obtenerPromedioInscripcionesPorEvento(LocalDate fechaDesde, LocalDate fechaHasta) {
        Integer cantidadEventos = obtenerCantidadEventos(fechaDesde, fechaHasta);
        Integer cantidadInscripcionesTotales = obtenerCantidadInscripcionesTotales(fechaDesde, fechaHasta);
        
        Double promedio = cantidadEventos > 0 ?
                cantidadInscripcionesTotales.doubleValue() / cantidadEventos : 0.0;
        
        return Math.round(promedio * 100.0) / 100.0;
    }

    /**
     * Encuentra el evento con más inscripciones
     */
    private String encontrarEventoMasPopular(List<Evento> eventos, List<Inscripcion> inscripciones) {
        if (eventos.isEmpty()) {
            return "No hay eventos disponibles";
        }

        // Contar inscripciones por evento
        Map<String, Long> inscripcionesPorEvento = inscripciones.stream()
                .collect(Collectors.groupingBy(
                    inscripcion -> inscripcion.evento().id(),
                    Collectors.counting()
                ));

        // Encontrar el evento con más inscripciones
        return eventos.stream()
                .max((e1, e2) -> {
                    Long count1 = inscripcionesPorEvento.getOrDefault(e1.id(), 0L);
                    Long count2 = inscripcionesPorEvento.getOrDefault(e2.id(), 0L);
                    return count1.compareTo(count2);
                })
                .map(Evento::titulo)
                .orElse("No hay eventos disponibles");
    }

    /**
     * Calcula la tasa de conversión desde waitlist
     * @param totalInscripciones Total de inscripciones
     * @param inscripcionesConfirmadas Inscripciones confirmadas/aceptadas
     * @return Porcentaje de aceptación
     */
    public static double calcularTasaConversion(Integer totalInscripciones, Integer inscripcionesConfirmadas) {
        if (totalInscripciones == null || inscripcionesConfirmadas == null) return 0.0;
        if (totalInscripciones <= 0) return 0.0;
        double percent = ( inscripcionesConfirmadas.doubleValue() /  totalInscripciones) * 100.0;

        // truncado a 2 decimales (sin redondear)
        return Math.floor(percent * 100.0) / 100.0;
    }

    private List<Evento> filtrarEventosPorFecha(List<Evento> eventos, LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde == null && fechaHasta == null) {
            return eventos;
        }
        
        return eventos.stream()
                .filter(evento -> {
                    LocalDate fechaEvento = evento.fecha().toLocalDate();
                    boolean despuesDeFechaDesde = fechaDesde == null || !fechaEvento.isBefore(fechaDesde);
                    boolean antesDeFechaHasta = fechaHasta == null || !fechaEvento.isAfter(fechaHasta);
                    return despuesDeFechaDesde && antesDeFechaHasta;
                })
                .toList();
    }

    private List<Inscripcion> filtrarInscripcionesPorFecha(List<Inscripcion> inscripciones, LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde == null && fechaHasta == null) {
            return inscripciones;
        }
        
        return inscripciones.stream()
                .filter(inscripcion -> {
                    LocalDate fechaInscripcion = inscripcion.fechaRegistro().toLocalDate();
                    boolean despuesDeFechaDesde = fechaDesde == null || !fechaInscripcion.isBefore(fechaDesde);
                    boolean antesDeFechaHasta = fechaHasta == null || !fechaInscripcion.isAfter(fechaHasta);
                    return despuesDeFechaDesde && antesDeFechaHasta;
                })
                .toList();
    }
}
