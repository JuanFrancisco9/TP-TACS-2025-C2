package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.FiltrosDTO;
import org.utn.ba.tptacsg2.dtos.output.ResultadoBusquedaEvento;
import org.utn.ba.tptacsg2.exceptions.EventoUpdateInvalidoException;
import org.utn.ba.tptacsg2.helpers.EventPredicateBuilder;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;
import org.utn.ba.tptacsg2.repositories.db.*;
import org.w3c.dom.events.EventException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class EventoService {
    private final EventoRepositoryDB eventoRepository;
    private final InscripcionRepositoryDB inscripcionRepository;
    private final OrganizadorRepositoryDB organizadorRepository;
    private final GeneradorIDService generadorIDService;
    private final EstadoEventoRepositoryDB estadoEventoRepository;
    private final CategoriaService categoriaService;
    private final EstadoInscripcionRepositoryDB estadoInscripcionRepository;
    @Value("${app.pagination.default-page-size}")
    private Integer tamanioPagina;

    @Autowired
    public EventoService(EventoRepositoryDB eventoRepository, InscripcionRepositoryDB inscripcionRepository, OrganizadorRepositoryDB organizadorRepository, GeneradorIDService generadorIDService, EstadoEventoRepositoryDB estadoEventoRepository, CategoriaService categoriaService, EstadoInscripcionRepositoryDB estadoInscripcionRepository) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.organizadorRepository = organizadorRepository;
        this.generadorIDService = generadorIDService;
        this.estadoEventoRepository = estadoEventoRepository;
        this.categoriaService = categoriaService;
        this.estadoInscripcionRepository = estadoInscripcionRepository;
    }

    public Integer cuposDisponibles(Evento evento) {
        return evento.cupoMaximo() -  inscripcionRepository.findByEvento_Id(evento.id())
                .stream().filter(inscripcion -> inscripcion.estado().getTipoEstado().equals(TipoEstadoInscripcion.ACEPTADA))
                .toList().size();
    }

    public Evento registrarEvento(SolicitudEvento solicitud) {
        Organizador organizador = organizadorRepository.findById(solicitud.organizadorId())
                .orElseThrow(() -> new RuntimeException("Organizador no encontrado"));

        EstadoEvento estadoInicial = new EstadoEvento(this.generadorIDService.generarID(), solicitud.estado(), LocalDateTime.now());

        Evento evento = new Evento(
                generadorIDService.generarID(),
                solicitud.titulo(),
                solicitud.descripcion(),
                solicitud.fecha(),
                solicitud.horaInicio(),
                solicitud.duracion(),
                solicitud.ubicacion(),
                solicitud.cupoMaximo(),
                solicitud.cupoMinimo(),
                solicitud.precio(),
                organizador,
                estadoInicial,
                solicitud.categoria(),
                solicitud.etiquetas()
        );

        estadoInicial.setEvento(evento);
        this.estadoEventoRepository.save(estadoInicial);
        eventoRepository.save(evento);

        this.categoriaService.guardarCategoria(solicitud.categoria());

        return evento;
    }

    public Evento actualizarEvento(String idEvento, Evento eventoUpdate) {
        if(eventoRepository.findById(idEvento).isPresent()) {
            isUpdateValido(idEvento, eventoUpdate);

            Evento eventoActualizado = new Evento(
                    eventoUpdate.id(),
                    eventoUpdate.titulo(),
                    eventoUpdate.descripcion(),
                    eventoUpdate.fecha(),
                    eventoUpdate.horaInicio(),
                    eventoUpdate.duracion(),
                    eventoUpdate.ubicacion(),
                    eventoUpdate.cupoMaximo(),
                    eventoUpdate.cupoMinimo(),
                    eventoUpdate.precio(),
                    eventoUpdate.organizador(),
                    eventoUpdate.estado(),
                    eventoUpdate.categoria(),
                    eventoUpdate.etiquetas()
        );

        eventoRepository.save(eventoActualizado);
        actualizarInscripciones(eventoActualizado, eventoActualizado.estado().getTipoEstado());

        return eventoActualizado;
        }else  {
            throw new RuntimeException("No existe el evento con el id: " + idEvento);
        }
    }

    private void isUpdateValido(String idEvent, Evento eventoUpdate) {
        Evento eventoActual = eventoRepository.findById(idEvent).orElse(null);

        int cantidadInscriptos = inscripcionRepository.findByEvento_Id(idEvent).size();

        if (eventoUpdate.cupoMaximo() < cantidadInscriptos) {
            throw new EventoUpdateInvalidoException("Evento tiene una cantidad de inscriptos mayor al cupo maximo que se desea setear");
        }
    }

    public Evento cambiarEstado(String idEvento,TipoEstadoEvento estado) {
        Evento evento = eventoRepository.findById(idEvento).orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        EstadoEvento estadoEvento = new EstadoEvento(this.generadorIDService.generarID(), estado, LocalDateTime.now());
        this.estadoEventoRepository.save(estadoEvento);

        Evento eventoActualizado = new Evento(
                evento.id(),
                evento.titulo(),
                evento.descripcion(),
                evento.fecha(),
                evento.horaInicio(),
                evento.duracion(),
                evento.ubicacion(),
                evento.cupoMaximo(),
                evento.cupoMinimo(),
                evento.precio(),
                evento.organizador(),
                estadoEvento,
                evento.categoria(),
                evento.etiquetas()
        );

        actualizarInscripciones(eventoActualizado, eventoActualizado.estado().getTipoEstado());

        eventoRepository.save(eventoActualizado);

        return eventoActualizado;
    }

    // Mucho texto pero no es para tanto
    private void actualizarInscripciones(Evento evento, TipoEstadoEvento tipoEstadoEvento) {
        // Obtengo todas las inscripciones que no estén canceladas (esas ya fueron, no me interesan) y  las ordena por orden de inscripcion
        List<Inscripcion> inscripciones = inscripcionRepository.findByEvento_Id(evento.id()).stream()
                .filter(i -> i.estado().getTipoEstado() != TipoEstadoInscripcion.CANCELADA)
                .sorted(Comparator.comparing(Inscripcion::fechaRegistro))
                .toList();

        switch (tipoEstadoEvento) {
            case CONFIRMADO -> { // Pasa todas las primeras n inscripciones (no canceladas) a CONFIRMADAS
                for(int i=0; i < Math.min(evento.cupoMaximo(), inscripciones.size()); i++) {
                    Inscripcion inscripcion = inscripciones.get(i);
                    estadoInscripcionRepository.save(inscripcion.estado().updateEstado(TipoEstadoInscripcion.ACEPTADA));
                }

                return;
            }
            case PENDIENTE -> { // Pasa todas las inscripciones en estado ACEPTADA a PENDIENTE
                inscripciones.stream()
                        .filter(i -> {return i.estado().getTipoEstado().equals(TipoEstadoInscripcion.ACEPTADA);})
                        .forEach(i -> {estadoInscripcionRepository.save(i.estado().updateEstado(TipoEstadoInscripcion.PENDIENTE));});
                return;
            }
            case CANCELADO -> { // Cancelo todas las inscripciones
                inscripciones.forEach(i -> {estadoInscripcionRepository.save(i.estado().updateEstado(TipoEstadoInscripcion.CANCELADA));});
                return;
            }
            case NO_ACEPTA_INSCRIPCIONES -> { // Do nothing
                return;
            }
            default -> {
                throw new RuntimeException("No existe el estado de evento: " + tipoEstadoEvento);
            }
        }
    }


    public Evento getEvento(String eventoId){
        return eventoRepository.findById(eventoId).orElseThrow(()-> new RuntimeException("Evento " + eventoId + " no encontrado"));
    }

    public List<Participante> getParticipantes(String eventoId){

        return inscripcionRepository.findParticipantesAceptadosPorEvento(eventoId);
    }

    public void cerrarEventosProximos() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(24);

        List<Evento> eventos = eventoRepository.findAll();

        eventos.stream()
                .filter(evento -> {
                    LocalDateTime fechaHoraEvento = LocalDateTime.of(LocalDate.from(evento.fecha()), LocalTime.parse(evento.horaInicio()));
                    return fechaHoraEvento.isBefore(limite);
                })
                .filter(evento -> !evento.estado().getTipoEstado().equals(TipoEstadoEvento.NO_ACEPTA_INSCRIPCIONES))
                .forEach(evento -> cambiarEstado(evento.id(), TipoEstadoEvento.NO_ACEPTA_INSCRIPCIONES));
    }

    //TODO refactor para que los predicados los haga desde la BD
    public ResultadoBusquedaEvento buscarEventos(FiltrosDTO filtros) {
        List<Evento> eventos = eventoRepository.findAll();

        Predicate<Evento> predicadosCombinados = new EventPredicateBuilder()
                .conRangoDeFecha(filtros.fechaDesde(), filtros.fechaHasta())
                .conCategoria(filtros.categoria())
                .conUbicacion(filtros.ubicacion())
                .conRangoDePrecios(filtros.precioMinimo(), filtros.precioMaximo())
                .conPalabrasClave(filtros.palabrasClave())
                .build();

        List<Evento> eventosFiltrados = eventos.stream().filter(predicadosCombinados).toList();

        Integer totalElementos = eventosFiltrados.size();
        Integer totalPaginas = (int) Math.ceil((double) totalElementos / tamanioPagina);
        Integer inicioEventos = filtros.nroPagina() * tamanioPagina;
        Integer finalEventos = Math.min(inicioEventos + tamanioPagina , eventosFiltrados.size());

        List<Evento> eventosFiltradosYPaginados = inicioEventos >= eventosFiltrados.size() ? new ArrayList<>() : eventosFiltrados.subList(inicioEventos, finalEventos);

        return new ResultadoBusquedaEvento(eventosFiltradosYPaginados, filtros.nroPagina() + 1, totalElementos, totalPaginas);
    }
}