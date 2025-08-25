package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.EstadisticasUso;

@Service
public class EstadisticasService {

    /**
     * Obtiene estadísticas de uso simuladas en memoria
     * Para la primera entrega - datos de ejemplo
     */
    public EstadisticasUso obtenerEstadisticasUso() {
        // Datos simulados para la primera entrega
        Long cantidadEventos = 15L;
        Long cantidadEventosActivos = 8L;
        Long cantidadInscripcionesTotales = 120L;
        Long cantidadInscripcionesConfirmadas = 95L;
        Long cantidadInscripcionesWaitlist = 25L;

        // Calcular tasa de conversión (ejemplo: 60% de conversión)
        Double tasaConversionWaitlist = calcularTasaConversion(25L, 15L);

        String eventoMasPopular = "TP TACS G2 - Primer Entrega";
        Double promedioInscripciones = cantidadInscripcionesTotales.doubleValue() / cantidadEventos.doubleValue();

        return new EstadisticasUso(
            cantidadEventos,
            cantidadEventosActivos,
            cantidadInscripcionesTotales,
            cantidadInscripcionesConfirmadas,
            cantidadInscripcionesWaitlist,
            tasaConversionWaitlist,
            eventoMasPopular,
            Math.round(promedioInscripciones * 100.0) / 100.0 // Redondear a 2 decimales
        );
    }

    /**
     * Calcula la tasa de conversión desde waitlist
     * @param totalWaitlist Total de inscripciones en waitlist
     * @param conversionesReales Inscripciones que pasaron de waitlist a confirmadas
     * @return Porcentaje de conversión
     */
    private Double calcularTasaConversion(Long totalWaitlist, Long conversionesReales) {
        if (totalWaitlist == null || totalWaitlist == 0) {
            return 0.0;
        }
        return Math.round((conversionesReales.doubleValue() / totalWaitlist.doubleValue()) * 100.0 * 100.0) / 100.0;
    }
}
