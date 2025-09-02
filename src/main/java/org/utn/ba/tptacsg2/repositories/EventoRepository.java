package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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
        EstadoEvento estadoEvento1 = this.estadoEventoRepository.getEstadoInscripcionById("1");
        EstadoEvento estadoEvento2 = this.estadoEventoRepository.getEstadoInscripcionById("2");

        this.guardarEvento(new Evento("0", "Seminario de Mocks", "Mocks", LocalDateTime.now(),
                            "19;00",5F, new Ubicacion("","","", ""), 10,
                            new Precio("Pesos", 100F), new Organizador("1","","",""), new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), new Categoria("Educativo")));

        // Segundo evento de prueba
        this.guardarEvento(new Evento("2", "Workshop de Testing", "Testing", LocalDateTime.now().plusDays(7),
                            "10:00",3F, new Ubicacion("","","", ""), 20,
                            new Precio("Pesos", 150F), new Organizador("2","María","González","87654321"), new EstadoEvento("2", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), new Categoria("Tecnología")));

        estadoEvento1.setEvento(this.eventos.get(0));
        estadoEvento2.setEvento(this.eventos.get(1));
    }
}
