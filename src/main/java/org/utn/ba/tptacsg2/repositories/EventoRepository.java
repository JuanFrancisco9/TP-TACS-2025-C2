package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.events.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EventoRepository {

    private final List<Evento> eventos = new ArrayList<>();

    public List<Evento> getEventos() {
        return eventos;
    }

    public void guardarEvento(Evento evento) {
        eventos.add(evento);
    }

    public Optional<Evento> getEvento(String id) {
        return this.eventos.stream()
                .filter(evento -> evento.id().equals(id))
                .findFirst();
    }

    public List<Evento> getEventosDeOrganizador(String idOrganizador) {
        return this.getEventos().stream()
                .filter(evento -> evento.organizador() != null &&
                        evento.organizador().id().equals(idOrganizador))
                .toList();
    }

    public List<Evento> getEventosPorCategoria(Categoria categoria) {
        return this.getEventos().stream()
                .filter(evento -> evento.categoria() != null &&
                        evento.categoria().equals(categoria))
                .toList();
    }

    public List<Evento> getEventosPorPalabraClave(List<String> palabrasClave) {
        return this.getEventos().stream()
                .filter(evento -> {
                    String tituloSinEspacios = evento.titulo().replaceAll("\\s+", "");
                    return palabrasClave.stream()
                            .allMatch(palabra -> tituloSinEspacios.contains(palabra));
                })
                .toList();
    }

    public List<Evento> getEventosPorFecha(LocalDateTime fecha) {
        LocalDateTime now = LocalDateTime.now();
        return this.getEventos().stream()
                .filter(evento -> evento.fecha().toLocalDate().isEqual(fecha.toLocalDate())
                        && evento.fecha().isAfter(now))
                .toList();
    }

    public List<Evento> getEventosPorRangoDePrecio (Float precioMin, Float precioMax) {
        return this.getEventos().stream()
                .filter(evento -> evento.precio() != null &&
                        evento.precio().cantidad() >= precioMin &&
                        evento.precio().cantidad() <= precioMax)
                .toList();

    }


    @PostConstruct
    public void initializeData() {
        this.guardarEvento(new Evento("0", "Seminario de Mocks", "Mocks", LocalDateTime.now(),
                            "19;00",5F, new Ubicacion("","",""), 10,
                            new Precio("Pesos", 100F), new Organizador("1","","",""), new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), new Categoria("Educativo")));

    }
}
