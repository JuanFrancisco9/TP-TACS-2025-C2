package org.utn.ba.tptacsg2.helpers;

import org.utn.ba.tptacsg2.models.events.Evento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

//TODO luego se convertira en Criteria para Mongo, no para la memoria
public class EventPredicateBuilder {

    private final List<Predicate<Evento>> predicados = new ArrayList<>();

    public EventPredicateBuilder conRangoDeFecha(LocalDate fechaDesde, LocalDate fechaHasta) {
        if(fechaDesde != null ) {
            predicados.add(evento -> !evento.fecha().toLocalDate().isBefore(fechaDesde));
        }

        if(fechaHasta != null ) {
            predicados.add(evento -> !evento.fecha().toLocalDate().isAfter(fechaHasta));
        }

        return this;
    }

    public EventPredicateBuilder conRangoDePrecios(Double precioMin, Double precioMax) {
        if(precioMin != null ) {
            predicados.add(evento -> evento.precio().cantidad() >= precioMin);
        }

        if(precioMax != null ) {
            predicados.add(evento -> evento.precio().cantidad() <= precioMax);
        }

        return this;
    }

    public EventPredicateBuilder conCategoria(String categoria) {
        if(categoria != null && !categoria.isEmpty()) {
            predicados.add(evento -> evento.categoria() != null && evento.categoria().getTipo().equalsIgnoreCase(categoria));
        }

        return this;
    }

    public EventPredicateBuilder conPalabrasClave(String palabrasClave) {
        if(palabrasClave != null && !palabrasClave.isEmpty()) {
            List<String> listaPalabras = Arrays.stream(palabrasClave.split(" "))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toLowerCase)
                    .toList();

            // Verficar si TODAS las keywords están presentes
            Predicate<Evento> predicadoPalabrasClave = evento -> {
                String tituloLower = evento.titulo().toLowerCase();
                String descripcionLower = evento.descripcion().toLowerCase();

                return listaPalabras.stream()
                        .allMatch(keyword -> tituloLower.contains(keyword) || descripcionLower.contains(keyword));
            };

            predicados.add(predicadoPalabrasClave);
        }

        return this;
    }

    public EventPredicateBuilder conUbicacion(String ubicacion) {

        if(ubicacion != null && !ubicacion.isEmpty()) {
            predicados.add(evento -> evento.ubicacion().localidad().equalsIgnoreCase(ubicacion));        }

        return this;
    }

    public Predicate<Evento> build() {
        return predicados.stream()
                .reduce(Predicate::and)
                .orElse(_ -> true);
    }

}
