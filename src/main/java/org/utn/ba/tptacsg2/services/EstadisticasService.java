package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.EstadisticasUso;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.EventoRepository;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {

    private final EventoRepository eventoRepository;
    private final InscripcionRepository inscripcionRepository;

    public EstadisticasService(EventoRepository eventoRepository, InscripcionRepository inscripcionRepository) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
    }


    public EstadisticasUso obtenerEstadisticasUso() {
        List<Evento> eventos = eventoRepository.getEventos();
        List<Inscripcion> inscripciones = inscripcionRepository.getInscripciones();


        Integer cantidadEventos = eventos.size();
        Integer cantidadEventosActivos = eventos.stream()
                .filter(evento -> evento.estado().getTipoEstado() == TipoEstadoEvento.CONFIRMADO)
                .toList().size();

        Integer cantidadInscripcionesTotales = inscripciones.size();
        Integer cantidadInscripcionesConfirmadas = inscripciones.stream()
                .filter(inscripcion -> inscripcion.estado().getTipoEstado() == TipoEstadoInscripcion.ACEPTADA)
                .toList().size();

        Integer cantidadInscripcionesPendientes = inscripciones.stream()
                .filter(inscripcion -> inscripcion.estado().getTipoEstado() == TipoEstadoInscripcion.PENDIENTE)
                .toList().size();


        Double tasaConversionWaitlist = calcularTasaConversion(cantidadInscripcionesTotales, cantidadInscripcionesConfirmadas);

        String eventoMasPopular = encontrarEventoMasPopular(eventos, inscripciones);

        // Calcular promedio de inscripciones por evento
        Double promedioInscripciones = cantidadEventos > 0 ?
                cantidadInscripcionesTotales.doubleValue() / cantidadEventos : 0.0;

        return new EstadisticasUso(
            cantidadEventos,
            cantidadEventosActivos,
            cantidadInscripcionesTotales,
            cantidadInscripcionesConfirmadas,
            cantidadInscripcionesPendientes, // Usando pendientes como waitlist
            tasaConversionWaitlist,
            eventoMasPopular,
            Math.round(promedioInscripciones * 100.0) / 100.0
        );
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
}
