package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.utn.ba.tptacsg2.dtos.EventoDTO;
import org.utn.ba.tptacsg2.dtos.FiltrosDTO;
import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;
import org.utn.ba.tptacsg2.dtos.output.ResultadoBusquedaEvento;
import org.utn.ba.tptacsg2.exceptions.EventoNoEncontradoException;
import org.utn.ba.tptacsg2.exceptions.EventoUpdateInvalidoException;
import org.utn.ba.tptacsg2.helpers.EventPredicateBuilder;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.Categoria;
import org.utn.ba.tptacsg2.models.events.EstadoEvento;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.events.Imagen;
import org.utn.ba.tptacsg2.models.events.SolicitudEvento;
import org.utn.ba.tptacsg2.models.events.Ubicacion;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;
import org.utn.ba.tptacsg2.models.location.Localidad;
import org.utn.ba.tptacsg2.models.location.Provincia;
import org.utn.ba.tptacsg2.repositories.db.EstadoEventoRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.EventoRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.InscripcionRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.OrganizadorRepositoryDB;
import org.utn.ba.tptacsg2.repositories.InscripcionRepository;
import org.utn.ba.tptacsg2.repositories.db.*;
import org.w3c.dom.events.EventException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Service
public class EventoService {
    private final EventoRepositoryDB eventoRepository;
    private final InscripcionRepositoryDB inscripcionRepository;
    private final OrganizadorRepositoryDB organizadorRepository;
    private final GeneradorIDService generadorIDService;
    private final EstadoEventoRepositoryDB estadoEventoRepository;
    private final CategoriaService categoriaService;
    private final R2StorageService r2StorageService;
    private final RedisCacheService redisCacheService;
    private final UbicacionCatalogService ubicacionCatalogService;
    private final EstadoInscripcionRepositoryDB estadoInscripcionRepository;
    @Value("${app.pagination.default-page-size}")
    private Integer tamanioPagina;
    private final Duration tiempoDeGracia;
    private static final List<String> ALLOWED_VIRTUAL_DOMAINS = List.of(
            "meet.google.com",
            "zoom.us",
            "zoom.com",
            "teams.microsoft.com",
            "msteams.link",
            "webex.com",
            "gotomeet.me",
            "gotomeeting.com",
            "whereby.com",
            "jitsi.org",
            "discord.com",
            "youtube.com",
            "youtu.be",
            "twitch.tv"
    );

    @Autowired
    public EventoService(EventoRepositoryDB eventoRepository,
                         InscripcionRepositoryDB inscripcionRepository,
                         OrganizadorRepositoryDB organizadorRepository,
                         GeneradorIDService generadorIDService,
                         EstadoEventoRepositoryDB estadoEventoRepository,
                         CategoriaService categoriaService,
                         R2StorageService r2StorageService,
                         RedisCacheService redisCacheService,
                         UbicacionCatalogService ubicacionCatalogService,
                         EstadoInscripcionRepositoryDB estadoInscripcionRepository) {
        this.eventoRepository = eventoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.organizadorRepository = organizadorRepository;
        this.generadorIDService = generadorIDService;
        this.estadoEventoRepository = estadoEventoRepository;
        this.categoriaService = categoriaService;
        this.r2StorageService = r2StorageService;
        this.redisCacheService = redisCacheService;
        this.ubicacionCatalogService = ubicacionCatalogService;
        this.tiempoDeGracia = Duration.ofHours(36);
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

        // Obtener o crear la categoría basándose en el nombre que viene del frontend
        Categoria categoria = this.categoriaService.obtenerOCrearCategoria(solicitud.categoria());

        Ubicacion ubicacionNormalizada = prepararUbicacion(solicitud.ubicacion());

        Evento evento = new Evento(
                generadorIDService.generarID(),
                solicitud.titulo(),
                solicitud.descripcion(),
                solicitud.fecha(),
                solicitud.horaInicio(),
                solicitud.duracion(),
                ubicacionNormalizada,
                solicitud.cupoMaximo(),
                solicitud.cupoMinimo(),
                solicitud.precio(),
                organizador,
                estadoInicial,
                categoria,
                solicitud.etiquetas(),
                null
        );

        estadoInicial.setEvento(evento);
        this.estadoEventoRepository.save(estadoInicial);
        this.redisCacheService.crearEventoConCupos(evento.id(), evento.cupoMaximo(), this.fechaExpiracionDeCache(evento));
        eventoRepository.save(evento);


        return evento;
    }

    public EventoDTO registrarEventoConImagen(SolicitudEvento solicitud, MultipartFile imagen) {
        Organizador organizador = organizadorRepository.findById(solicitud.organizadorId())
                .orElseThrow(() -> new RuntimeException("Organizador no encontrado"));

        EstadoEvento estadoInicial = new EstadoEvento(this.generadorIDService.generarID(), solicitud.estado(), LocalDateTime.now());

        String imagenKey = null;

        // Procesar imagen si está presente
        if (imagen != null && !imagen.isEmpty()) {
            try {
                // Validar tipo de archivo
                String contentType = imagen.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new RuntimeException("El archivo debe ser una imagen");
                }

                // Validar tamaño (máximo 5MB)
                if (imagen.getSize() > 5 * 1024 * 1024) {
                    throw new RuntimeException("El archivo no puede ser mayor a 5MB");
                }

                // Subir imagen a R2
                Long ownerUserId = null;
                if (solicitud.organizadorId() != null) {
                    try {
                        ownerUserId = Long.valueOf(solicitud.organizadorId());
                    } catch (NumberFormatException ignored) {
                        ownerUserId = null; // owner opcional cuando el ID no es numérico
                    }
                }

                Imagen imagenSubida = r2StorageService.upload(imagen, ownerUserId);
                imagenKey = imagenSubida.key();
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar la imagen: " + e.getMessage());
            }
        }

        if (solicitud.categoria() == null || solicitud.categoria().getTipo() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
        Categoria categoria = this.categoriaService.obtenerOCrearCategoria(solicitud.categoria());

        Ubicacion ubicacionNormalizada = prepararUbicacion(solicitud.ubicacion());

        Evento evento = new Evento(
                generadorIDService.generarID(),
                solicitud.titulo(),
                solicitud.descripcion(),
                solicitud.fecha(),
                solicitud.horaInicio(),
                solicitud.duracion(),
                ubicacionNormalizada,
                solicitud.cupoMaximo(),
                solicitud.cupoMinimo(),
                solicitud.precio(),
                organizador,
                estadoInicial,
                categoria,
                solicitud.etiquetas(),
                imagenKey
        );

        estadoInicial.setEvento(evento);
        this.estadoEventoRepository.save(estadoInicial);
        eventoRepository.save(evento);

        return mapearEventoDTO(evento);
    }

    private void validarUbicacionVirtual(Ubicacion ubicacion) {
        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicación del evento es obligatoria.");
        }

        if (ubicacion.esVirtual()) {
            String enlace = ubicacion.enlaceVirtual();
            if (enlace == null || enlace.isBlank()) {
                throw new IllegalArgumentException("Ingresá el enlace del evento virtual.");
            }

            URI uri;
            try {
                uri = new URI(enlace.trim());
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("El enlace del evento virtual no tiene un formato válido.");
            }

            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException("El enlace del evento virtual debe utilizar HTTPS.");
            }

            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                throw new IllegalArgumentException("El enlace del evento virtual debe incluir un dominio válido.");
            }

            String hostLower = host.toLowerCase();
            boolean permitido = ALLOWED_VIRTUAL_DOMAINS.stream()
                    .map(String::toLowerCase)
                    .anyMatch(dominio -> hostLower.equals(dominio) || hostLower.endsWith("." + dominio));

            if (!permitido) {
                throw new IllegalArgumentException("El dominio del enlace virtual no está permitido.");
            }

            if (uri.getUserInfo() != null && !uri.getUserInfo().isBlank()) {
                throw new IllegalArgumentException("El enlace del evento virtual no debe contener credenciales.");
            }
        }
    }

    private Ubicacion prepararUbicacion(Ubicacion ubicacion) {
        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicación del evento es obligatoria.");
        }

        if (ubicacion.esVirtual()) {
            validarUbicacionVirtual(ubicacion);
            String enlace = ubicacion.enlaceVirtual() == null ? null : ubicacion.enlaceVirtual().trim();
            return Ubicacion.virtual(enlace);
        }

        return normalizarUbicacionPresencial(ubicacion);
    }

    private Ubicacion normalizarUbicacionPresencial(Ubicacion ubicacion) {
        String provinciaNombre = ubicacion.provincia() != null ? ubicacion.provincia().trim() : "";
        if (provinciaNombre.isEmpty()) {
            throw new IllegalArgumentException("Seleccioná una provincia válida.");
        }

        Provincia provincia = ubicacionCatalogService.buscarProvinciaPorNombre(provinciaNombre)
                .orElseThrow(() -> new IllegalArgumentException("La provincia " + provinciaNombre + " no está registrada."));

        String localidadNombre = ubicacion.localidad() != null ? ubicacion.localidad().trim() : "";
        if (localidadNombre.isEmpty()) {
            throw new IllegalArgumentException("Seleccioná una localidad válida.");
        }

        Localidad localidad = ubicacionCatalogService.buscarLocalidadPorNombreYProvincia(localidadNombre, provincia.getId())
                .orElseThrow(() -> new IllegalArgumentException("La localidad " + localidadNombre + " no existe en la provincia " + provincia.getNombre() + "."));

        String direccion = ubicacion.direccion() != null ? ubicacion.direccion().trim() : "";
        if (direccion.isEmpty()) {
            throw new IllegalArgumentException("Ingresá la dirección del evento presencial.");
        }

        String latitud = ubicacion.latitud();
        String longitud = ubicacion.longitud();

        if ((latitud == null || latitud.isBlank()) && localidad.getLatitud() != null) {
            latitud = localidad.getLatitud().toString();
        }
        if ((longitud == null || longitud.isBlank()) && localidad.getLongitud() != null) {
            longitud = localidad.getLongitud().toString();
        }

        return new Ubicacion(
                latitud != null && !latitud.isBlank() ? latitud.trim() : null,
                longitud != null && !longitud.isBlank() ? longitud.trim() : null,
                provincia.getNombre(),
                localidad.getNombre(),
                direccion,
                false,
                null
        );
    }

    public Instant fechaExpiracionDeCache(Evento evento) {
        return evento.fecha()
                .plus(tiempoDeGracia)
                .atZone(ZoneId.from(evento.fecha()))
                .toInstant();
    }

    public Evento actualizarEvento(String idEvento, Evento eventoUpdate) {
        Evento eventoExistente = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("No existe el evento con el id: " + idEvento));

        Ubicacion ubicacionNormalizada = prepararUbicacion(eventoUpdate.ubicacion());

        // Verificar si el estado cambió
        EstadoEvento estadoFinal;
        if (eventoExistente.estado().getTipoEstado().equals(eventoUpdate.estado().getTipoEstado())) {
            // El estado no cambió, reutilizar el estado existente
            estadoFinal = eventoExistente.estado();
        } else {
            // El estado cambió, crear un nuevo EstadoEvento
            estadoFinal = new EstadoEvento(
                    this.generadorIDService.generarID(),
                    eventoUpdate.estado().getTipoEstado(),
                    LocalDateTime.now()
            );
            // Guardar el nuevo estado antes de asociarlo
            this.estadoEventoRepository.save(estadoFinal);
        }

        // Manejar la categoría de la misma forma que en registrarEvento
        Categoria categoriaFinal;
        if (eventoUpdate.categoria() != null && eventoUpdate.categoria().getTipo() != null) {
            categoriaFinal = this.categoriaService.obtenerOCrearCategoria(eventoUpdate.categoria());
        } else {
            categoriaFinal = eventoExistente.categoria();
        }

        Evento eventoActualizado = new Evento(
                eventoUpdate.id(),
                eventoUpdate.titulo(),
                eventoUpdate.descripcion(),
                eventoUpdate.fecha(),
                eventoUpdate.horaInicio(),
                eventoUpdate.duracion(),
                ubicacionNormalizada,
                eventoUpdate.cupoMaximo(),
                eventoUpdate.cupoMinimo(),
                eventoUpdate.precio(),
                eventoUpdate.organizador(),
                estadoFinal,
                categoriaFinal,
                eventoUpdate.etiquetas(),
                eventoUpdate.imagenKey()
        );

        // Si se creó un nuevo estado, asociarlo con el evento
        if (!eventoExistente.estado().getTipoEstado().equals(eventoUpdate.estado().getTipoEstado())) {
            estadoFinal.setEvento(eventoActualizado);
            this.estadoEventoRepository.save(estadoFinal);
        }

        eventoRepository.save(eventoActualizado);
        actualizarInscripciones(eventoActualizado, eventoActualizado.estado().getTipoEstado());

        return eventoActualizado;
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
                evento.etiquetas(),
                evento.imagenKey()
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

        // Convertir eventos a DTOs con URLs de imagen
        List<EventoDTO> eventosDTO = eventosFiltradosYPaginados.stream()
                .map(this::mapearEventoDTO)
                .toList();

        return new ResultadoBusquedaEvento(eventosDTO, filtros.nroPagina() + 1, totalElementos, totalPaginas);
    }

    public EventoDTO obtenerEventoPorId(String eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventoNoEncontradoException("No se encontró el evento con ID: " + eventoId));
        return mapearEventoDTO(evento);
    }

    public EventoDTO mapearEventoDTO(Evento evento) {
        String imagenUrl = null;
        if (evento.imagenKey() != null) {
            imagenUrl = r2StorageService.getImageUrl(evento.imagenKey());
        }

        return new EventoDTO(
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
                evento.estado(),
                evento.categoria(),
                imagenUrl,
                evento.imagenKey()
        );
    }
}
