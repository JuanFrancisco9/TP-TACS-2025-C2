package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.events.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EventoRepository {

    private final List<Evento> eventos = new ArrayList<>();
    private final EstadoEventoRepository estadoEventoRepository;

    @Autowired
    public EventoRepository(EstadoEventoRepository estadoEventoRepository) {
        this.estadoEventoRepository = estadoEventoRepository;
    }

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

    public void actualizarEvento(Evento evento){
        eventos.removeIf(e -> e.id().equals(evento.id()));
        this.guardarEvento(evento);
    }

    @PostConstruct
    public void initializeData() {
        // Primer evento de prueba
        EstadoEvento estadoEvento1 = new EstadoEvento("1",TipoEstadoEvento.CONFIRMADO, LocalDateTime.now().minusDays(7));
        EstadoEvento estadoEvento2 = new EstadoEvento("2",TipoEstadoEvento.CONFIRMADO, LocalDateTime.now().minusDays(7));

        Evento evento1 = new Evento("0", "Seminario de Mocks", "Mocks", LocalDateTime.now(),
                "19;00",5F, new Ubicacion("-32.05322857239074", "-58.61824002335356","CABA", "Av. Press. Figueroa Alcorta 2099"), 10, 0,
                new Precio("Pesos", 100F), new Organizador("1","","","", null),
                estadoEvento1 ,new Categoria("Educativo"),new ArrayList<>());

        Evento evento2 = new Evento("2", "Workshop de Testing", "Testing", LocalDateTime.now().plusDays(7),
                "10:00", 3F, new Ubicacion("-32.05322857239074", "-58.61824002335356","CABA", "Av. Press. Figueroa Alcorta 2099"), 20, 0,
                new Precio("Pesos", 150F), new Organizador("2","María","González","87654321", null),
                estadoEvento2, new Categoria("Tecnología"), new ArrayList<>());

        this.guardarEvento(evento1);
        this.guardarEvento(evento2);

        estadoEvento1.setEvento(evento1);
        estadoEvento2.setEvento(evento2);

    }
}
