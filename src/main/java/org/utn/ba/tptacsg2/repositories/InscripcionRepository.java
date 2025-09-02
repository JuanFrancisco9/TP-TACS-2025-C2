package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.*;
import org.utn.ba.tptacsg2.models.inscriptions.*;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class InscripcionRepository {

    private final List<Inscripcion> inscripciones;
    private EstadoInscripcionRepository estadoInscripcionRepository;

    public InscripcionRepository(EstadoInscripcionRepository estadoInscripcionRepository) {
        this.inscripciones = new ArrayList<>();
        this.estadoInscripcionRepository = estadoInscripcionRepository;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }


    public Optional<Inscripcion> getInscripcionById(String id) {
        return this.getInscripciones().stream()
                .filter(inscripcion -> inscripcion.id().equals(id))
                .findFirst();
    }

    public void guardarInscripcion(Inscripcion inscripcion) {
        inscripciones.add(inscripcion);
    }

    public List<Inscripcion> getInscripcionesAEvento(Evento evento) {
        return this.getInscripciones().stream()
                .filter(inscripcion -> inscripcion.evento().id().equals(evento.id()))
                .toList();
    }

    public List<Inscripcion> getWailist(Evento evento) {
        return this.getInscripciones().stream().filter(i -> i.evento().id().equals(evento.id()) && i.estado().getTipoEstado() == TipoEstadoInscripcion.PENDIENTE).toList();
    }

    public Inscripcion getPrimerInscripcionDeWaitlist (Evento evento) {

        List<Inscripcion> inscripcionesEnWaitlist = this.getWailist(evento);

        return inscripcionesEnWaitlist.stream().min(Comparator.comparing(Inscripcion::fechaRegistro)).orElse(null);

    }
    public void actualizarInscripcion(Inscripcion inscripcion) {

        Inscripcion inscripcionEnBD = this.inscripciones.stream()
                .filter(i -> i.id().equals(inscripcion.id()))
                .findFirst()
                .orElse(null);

        this.inscripciones.remove(inscripcionEnBD);
        this.inscripciones.add(inscripcion);

    }

    public List<Inscripcion> getInscripcionesDeParticipante(String idParticipante) {
        return this.getInscripciones().stream()
                .filter(inscripcion -> inscripcion.participante() != null &&
                        inscripcion.participante().id().equals(idParticipante))
                .toList();
    }


    @PostConstruct
    public void initializeData() {
        // Crear participantes de prueba
        Participante participante1 = new Participante("1", "Carlos", "López", "11111111");
        Participante participante2 = new Participante("2", "Ana", "Martínez", "22222222");

        // Crear evento de prueba (similar al del EventoRepository)
        Organizador organizador = new Organizador("1", "Juan", "Pérez", "12345678");

        Evento evento = new Evento("0", "Seminario de Mocks", "Mocks", LocalDateTime.now(),
                "19:00", 5F, new Ubicacion("", "", "", ""), 10,0,
                new Precio("Pesos", 100F), organizador,
                new EstadoEvento("1", TipoEstadoEvento.CONFIRMADO, LocalDateTime.now()), new Categoria("Educativo"), new ArrayList<>());

        EstadoInscripcion estadoAceptada = estadoInscripcionRepository.getEstadoInscripcionById("1");
        EstadoInscripcion estadoPendiente = estadoInscripcionRepository.getEstadoInscripcionById("2");

        this.guardarInscripcion(new Inscripcion("1", participante1, LocalDateTime.now(), estadoAceptada, evento));
        this.guardarInscripcion(new Inscripcion("2", participante2, LocalDateTime.now(), estadoPendiente, evento));

        estadoAceptada.setInscripcion(this.inscripciones.get(0));
        estadoPendiente.setInscripcion(this.inscripciones.get(1));
    }
}
