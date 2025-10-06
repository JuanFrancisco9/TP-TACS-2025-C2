package org.utn.ba.tptacsg2.config;

import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.utn.ba.tptacsg2.dtos.TipoEstadoEvento;
import org.utn.ba.tptacsg2.models.actors.Organizador;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.events.*;
import org.utn.ba.tptacsg2.models.inscriptions.*;
import org.utn.ba.tptacsg2.models.location.Localidad;
import org.utn.ba.tptacsg2.models.location.Provincia;
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.repositories.db.*; // Asume que tienes todos los repositorios definidos
import org.utn.ba.tptacsg2.services.EventoService;
import org.utn.ba.tptacsg2.services.RedisCacheService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(
        name = "spring.application.db-init-enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UsuarioRepositoryDB usuarioRepository;
    private final OrganizadorRepositoryDB organizadorRepository;
    private final ParticipanteRepositoryDB participanteRepository;
    private final EventoRepositoryDB eventoRepositoryDB;
    private final CategoriaRepositoryDB categoriaRepositoryDB;
    private final EstadoEventoRepositoryDB estadoEventoRepository;
    private final InscripcionRepositoryDB inscripcionRepository;
    private final EstadoInscripcionRepositoryDB estadoInscripcionRepositoryDB;
    private final PasswordEncoder passwordEncoder;
    private final ProvinciaRepositoryDB provinciaRepositoryDB;
    private final LocalidadRepositoryDB localidadRepositoryDB;
    private final RedisCacheService redisCacheService;
    private final EventoService eventoService;

    public DatabaseSeeder(
            UsuarioRepositoryDB usuarioRepository,
            OrganizadorRepositoryDB organizadorRepository,
            ParticipanteRepositoryDB participanteRepository,
            EventoRepositoryDB eventoRepositoryDB,
            CategoriaRepositoryDB categoriaRepositoryDB,
            EstadoEventoRepositoryDB estadoEventoRepository,
            InscripcionRepositoryDB inscripcionRepository,
            EstadoInscripcionRepositoryDB estadoInscripcionRepositoryDB,
            PasswordEncoder passwordEncoder,
            ProvinciaRepositoryDB provinciaRepositoryDB,
            LocalidadRepositoryDB localidadRepositoryDB,
            RedisCacheService redisCacheService,
            EventoService eventoService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.organizadorRepository = organizadorRepository;
        this.participanteRepository = participanteRepository;
        this.eventoRepositoryDB = eventoRepositoryDB;
        this.categoriaRepositoryDB = categoriaRepositoryDB;
        this.estadoEventoRepository = estadoEventoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.estadoInscripcionRepositoryDB = estadoInscripcionRepositoryDB;
        this.passwordEncoder = passwordEncoder;
        this.provinciaRepositoryDB = provinciaRepositoryDB;
        this.localidadRepositoryDB = localidadRepositoryDB;
        this.redisCacheService = redisCacheService;
        this.eventoService = eventoService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando la inicialización de la base de datos...");

        cleanUp();

        seedProvinciasYLocalidades();

        Map<String, Categoria> categoriasPorTipo = seedCategorias();

        Usuario admin = new Usuario( "admin", "$argon2id$v=19$m=65536,t=4,p=1$Y6QXibp2pRk+u6XDSSX6Wg$vaFyKiCj6Tvl06OGHuJtPaw5+4iZDi4f2iN0jrsYYLs", Rol.ROLE_ADMIN);
        Usuario userPart = new Usuario( "usuario", "$argon2id$v=19$m=65536,t=4,p=1$hC1J7qKqgmkSUfl8kMdQow$wva2eKpy3Mw8/oJPvJw5JdPse+cEJ73EdmcT6uhcXmU", Rol.ROLE_USER);
        Usuario userOrg = new Usuario( "organizador", "$argon2id$v=19$m=65536,t=4,p=1$IDXLIGuWc88CLL+7VyhCOA$CXr5e1xeozTTolyjDn1PNX1cs9uHqXFbH6TrtDKOCtk", Rol.ROLE_ORGANIZER);

        admin = usuarioRepository.save(admin);
        userPart = usuarioRepository.save(userPart);
        userOrg = usuarioRepository.save(userOrg);

        Organizador organizador = new Organizador("Juan", "Perez", "12345678", userOrg);
        Participante participante = new Participante("Maria", "Gomez", "87654321", userPart);

        organizador = organizadorRepository.save(organizador);
        participante = participanteRepository.save(participante);

        //Nuestros usuraios participantes
        Usuario juanF = new Usuario("Juan F", passwordEncoder.encode("Juan F"), Rol.ROLE_USER);
        juanF = usuarioRepository.save(juanF);
        Participante juanFParticiapnte = new Participante("Juan Francisco", "Cáceres", "87654321", juanF);
        juanFParticiapnte = participanteRepository.save(juanFParticiapnte);

        Usuario valen = new Usuario("Valen", passwordEncoder.encode("Valen"), Rol.ROLE_USER);
        valen = usuarioRepository.save(valen);
        Participante valenParticiapnte = new Participante("Valentina", "Albiero", "87654321", valen);
        valenParticiapnte = participanteRepository.save(valenParticiapnte);

        Usuario aylu = new Usuario("Aylu", passwordEncoder.encode("Aylu"), Rol.ROLE_USER);
        aylu = usuarioRepository.save(aylu);
        Participante ayluParticiapnte = new Participante("Aylen", "Sandoval", "87654321", aylu);
        ayluParticiapnte = participanteRepository.save(ayluParticiapnte);

        Usuario juanma = new Usuario("Juanma", passwordEncoder.encode("Juanma"), Rol.ROLE_USER);
        juanma = usuarioRepository.save(juanma);
        Participante juanmaParticiapnte = new Participante("Juan Manuel", "Prividera", "87654321", juanma);
        juanmaParticiapnte = participanteRepository.save(juanmaParticiapnte);

        Usuario tomi = new Usuario("Tomi", passwordEncoder.encode("Tomi"), Rol.ROLE_USER);
        tomi = usuarioRepository.save(tomi);
        Participante tomiParticiapnte = new Participante("Tomas", "Pauza Sager", "87654321", tomi);
        tomiParticiapnte = participanteRepository.save(tomiParticiapnte);

        Usuario lucas = new Usuario("Lucas", passwordEncoder.encode("Lucas"), Rol.ROLE_USER);
        lucas = usuarioRepository.save(lucas);
        Participante lucasParticiapnte = new Participante("Lucas Manuel", "Vazquez", "87654321", lucas);
        lucasParticiapnte = participanteRepository.save(lucasParticiapnte);

        log.info("Usuarios, Organizadores y Participantes creados.");

        EstadoEvento estadoEvento = new EstadoEvento(
                TipoEstadoEvento.CONFIRMADO,
                LocalDateTime.now()
        );

        estadoEvento = estadoEventoRepository.save(estadoEvento);

        Ubicacion ubicacion = Ubicacion.presencial("-34.6037", "-58.3816", "Ciudad Autónoma de Buenos Aires", "CABA", "Av. 9 de Julio 1234");
        Precio precio = new Precio("ARS", 1500.0f);
        Categoria categoria = categoriaPorTipo(categoriasPorTipo, "Tecnología");

        Evento evento = new Evento(
                "15616515611",
                "Charla sobre microservicios con Spring Boot y MongoDB.",
                "Un Evento que no te podes perder",
                LocalDateTime.now().plusDays(7),
                "10:00",
                2.5f,
                ubicacion,
                100,
                10,
                precio,
                organizador,
                estadoEvento,
                categoria,
                Arrays.asList("Java", "Spring", "Microservicios"),
                null
        );

        evento = eventoRepositoryDB.save(evento);
        redisCacheService.crearEventoConCupos(evento.id(), evento.cupoMaximo(), eventoService.fechaExpiracionDeCache(evento));


        estadoEvento.setEvento(evento);
        estadoEventoRepository.save(estadoEvento);

        EstadoEvento estadoEvento2 = new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.now());
        estadoEvento2 = estadoEventoRepository.save(estadoEvento2);
        Categoria categoria2 = categoriaPorTipo(categoriasPorTipo, "Entretenimiento");

        Evento evento2 = new Evento(
                "700-151891-02068",
                "G2 en el Movistar Arena",
                "Un evento familiar que no te podes perder",
                LocalDateTime.now().plusDays(7),
                "11:00",
                2.5f,
                Ubicacion.presencial("30","30","Ciudad Autónoma de Buenos Aires","CABA", "Av. La plata 800"),
                100,
                10,
                precio,
                organizador,
                estadoEvento2,
                categoria2,
                Arrays.asList("Familia", "Comedia", "Entretenimiento"),
                null
        );

        evento2 = eventoRepositoryDB.save(evento2);
        estadoEvento2.setEvento(evento2);
        estadoEventoRepository.save(estadoEvento2);
        redisCacheService.crearEventoConCupos(evento.id(), evento.cupoMaximo(), eventoService.fechaExpiracionDeCache(evento2));

        EstadoEvento estadoEvento3 = new EstadoEvento(TipoEstadoEvento.CONFIRMADO, LocalDateTime.now());
        estadoEvento3 = estadoEventoRepository.save(estadoEvento3);
        Categoria categoria3 = categoriaPorTipo(categoriasPorTipo, "Turismo");
        Evento evento3 = new Evento(
                "700-02068",
                "Caminito",
                "Recorrer caminito a la luz de las velas",
                LocalDateTime.now().plusDays(7),
                "11:00",
                2.5f,
                Ubicacion.presencial("30","30","Ciudad Autónoma de Buenos Aires","CABA", "Av. Independencia 800"),
                100,
                10,
                new Precio("ARS", 0.f),
                organizador,
                estadoEvento3,
                categoria3,
                Arrays.asList("Luz", "Velas", "Caminata"),
                null
        );

        evento3 = eventoRepositoryDB.save(evento3);
        redisCacheService.crearEventoConCupos(evento.id(), evento.cupoMaximo(), eventoService.fechaExpiracionDeCache(evento3));

        estadoEvento3.setEvento(evento3);
        estadoEventoRepository.save(estadoEvento3);

        log.info("Evento y EstadoEvento creados.");


        EstadoInscripcion estadoInscripcion = new EstadoInscripcion(
                "885558888",
                TipoEstadoInscripcion.ACEPTADA,
                LocalDateTime.now()
        );
        estadoInscripcion = estadoInscripcionRepositoryDB.save(estadoInscripcion);

        Inscripcion inscripcion = new Inscripcion(
                participante,
                LocalDateTime.now(),
                estadoInscripcion,
                evento
        );


        inscripcion = inscripcionRepository.save(inscripcion);


        estadoInscripcion.setInscripcion(inscripcion);
        estadoInscripcionRepositoryDB.save(estadoInscripcion);

        log.info("Inscripción y EstadoInscripcion creados.");

        log.info("Base de datos inicializada con datos de ejemplo.");
    }

    private Map<String, Categoria> seedCategorias() {
        List<Categoria> categorias = List.of(
                new Categoria("Arte y Cultura", "Palette"),
                new Categoria("Arte Digital", "Brush"),
                new Categoria("Bienestar", "SelfImprovement"),
                new Categoria("Ciencia e Innovación", "Science"),
                new Categoria("Cine y Series", "Movie"),
                new Categoria("Comunidad y Encuentros", "Group"),
                new Categoria("Conferencia", "Event"),
                new Categoria("Deportes", "SportsSoccer"),
                new Categoria("Educación", "School"),
                new Categoria("Entretenimiento", "TheaterComedy"),
                new Categoria("Familia y Niños", "Groups"),
                new Categoria("Fiestas y Celebraciones", "Celebration"),
                new Categoria("Gaming y eSports", "SportsEsports"),
                new Categoria("Gastronomía", "Restaurant"),
                new Categoria("Mascotas", "Pets"),
                new Categoria("Música", "MusicNote"),
                new Categoria("Negocios y Networking", "Handshake"),
                new Categoria("Outdoor y Naturaleza", "Landscape"),
                new Categoria("Salud y Fitness", "FitnessCenter"),
                new Categoria("Tecnología", "Terminal"),
                new Categoria("Turismo", "TravelExplore"),
                new Categoria("Voluntariado", "VolunteerActivism")
        );

        List<Categoria> guardadas = categoriaRepositoryDB.saveAll(categorias);
        log.info("Catálogo de categorías inicializado ({} categorías).", guardadas.size());

        return guardadas.stream()
                .collect(Collectors.toMap(
                        categoria -> categoria.getTipo().toLowerCase(Locale.ROOT),
                        categoria -> categoria,
                        (existente, duplicado) -> existente
                ));
    }

    private Categoria categoriaPorTipo(Map<String, Categoria> categoriasPorTipo, String tipo) {
        Categoria categoria = categoriasPorTipo.get(tipo.toLowerCase(Locale.ROOT));
        if (categoria == null) {
            throw new IllegalStateException("No se encontró la categoría predefinida: " + tipo);
        }
        return categoria;
    }

    private void cleanUp() {
        inscripcionRepository.deleteAll();
        estadoInscripcionRepositoryDB.deleteAll();
        eventoRepositoryDB.deleteAll();
        categoriaRepositoryDB.deleteAll();
        estadoEventoRepository.deleteAll();
        participanteRepository.deleteAll();
        organizadorRepository.deleteAll();
        usuarioRepository.deleteAll();
        localidadRepositoryDB.deleteAll();
        provinciaRepositoryDB.deleteAll();
        log.info("Colecciones limpiadas.");
    }

    private void seedProvinciasYLocalidades() {
        List<Provincia> provincias = List.of(
                new Provincia(null, "Ciudad Autónoma de Buenos Aires"),
                new Provincia(null, "Buenos Aires"),
                new Provincia(null, "Catamarca"),
                new Provincia(null, "Chaco"),
                new Provincia(null, "Chubut"),
                new Provincia(null, "Córdoba"),
                new Provincia(null, "Corrientes"),
                new Provincia(null, "Entre Ríos"),
                new Provincia(null, "Formosa"),
                new Provincia(null, "Jujuy"),
                new Provincia(null, "La Pampa"),
                new Provincia(null, "La Rioja"),
                new Provincia(null, "Mendoza"),
                new Provincia(null, "Misiones"),
                new Provincia(null, "Neuquén"),
                new Provincia(null, "Río Negro"),
                new Provincia(null, "Salta"),
                new Provincia(null, "San Juan"),
                new Provincia(null, "San Luis"),
                new Provincia(null, "Santa Cruz"),
                new Provincia(null, "Santa Fe"),
                new Provincia(null, "Santiago del Estero"),
                new Provincia(null, "Tierra del Fuego"),
                new Provincia(null, "Tucumán")
        );
        List<Provincia> provinciasGuardadas = provinciaRepositoryDB.saveAll(provincias);

        var provinciaIdPorNombre = provinciasGuardadas.stream()
                .collect(java.util.stream.Collectors.toMap(Provincia::getNombre, Provincia::getId));

        List<Localidad> localidades = List.of(
                new Localidad(null, provinciaIdPorNombre.get("Ciudad Autónoma de Buenos Aires"), "CABA", -34.603722, -58.381592),
                new Localidad(null, provinciaIdPorNombre.get("Ciudad Autónoma de Buenos Aires"), "Palermo", -34.571149, -58.4233),
                new Localidad(null, provinciaIdPorNombre.get("Ciudad Autónoma de Buenos Aires"), "Belgrano", -34.56255, -58.4585),

                new Localidad(null, provinciaIdPorNombre.get("Buenos Aires"), "La Plata", -34.92145, -57.954533),
                new Localidad(null, provinciaIdPorNombre.get("Buenos Aires"), "Mar del Plata", -38.00042, -57.5562),
                new Localidad(null, provinciaIdPorNombre.get("Buenos Aires"), "Bahía Blanca", -38.7196, -62.27243),
                new Localidad(null, provinciaIdPorNombre.get("Buenos Aires"), "San Isidro", -34.474145, -58.527663),

                new Localidad(null, provinciaIdPorNombre.get("Catamarca"), "San Fernando del Valle", -28.46957, -65.78524),
                new Localidad(null, provinciaIdPorNombre.get("Catamarca"), "Andalgalá", -27.57391, -66.31666),
                new Localidad(null, provinciaIdPorNombre.get("Catamarca"), "Tinogasta", -28.0629, -67.5631),

                new Localidad(null, provinciaIdPorNombre.get("Chaco"), "Resistencia", -27.4519, -58.987),
                new Localidad(null, provinciaIdPorNombre.get("Chaco"), "Presidencia Roque Sáenz Peña", -26.7852, -60.4388),
                new Localidad(null, provinciaIdPorNombre.get("Chaco"), "Villa Ángela", -27.5733, -60.7153),

                new Localidad(null, provinciaIdPorNombre.get("Chubut"), "Rawson", -43.30016, -65.10228),
                new Localidad(null, provinciaIdPorNombre.get("Chubut"), "Trelew", -43.2471, -65.3051),
                new Localidad(null, provinciaIdPorNombre.get("Chubut"), "Puerto Madryn", -42.7692, -65.0385),

                new Localidad(null, provinciaIdPorNombre.get("Córdoba"), "Córdoba Capital", -31.420083, -64.188776),
                new Localidad(null, provinciaIdPorNombre.get("Córdoba"), "Villa Carlos Paz", -31.420654, -64.499993),
                new Localidad(null, provinciaIdPorNombre.get("Córdoba"), "Río Cuarto", -33.123163, -64.349564),

                new Localidad(null, provinciaIdPorNombre.get("Corrientes"), "Corrientes Capital", -27.4691, -58.8306),
                new Localidad(null, provinciaIdPorNombre.get("Corrientes"), "Goya", -29.1401, -59.2626),
                new Localidad(null, provinciaIdPorNombre.get("Corrientes"), "Paso de los Libres", -29.7126, -57.0877),

                new Localidad(null, provinciaIdPorNombre.get("Entre Ríos"), "Paraná", -31.7319, -60.5238),
                new Localidad(null, provinciaIdPorNombre.get("Entre Ríos"), "Concordia", -31.3929, -58.0209),
                new Localidad(null, provinciaIdPorNombre.get("Entre Ríos"), "Gualeguaychú", -33.0092, -58.5172),

                new Localidad(null, provinciaIdPorNombre.get("Formosa"), "Formosa Capital", -26.185, -58.175),
                new Localidad(null, provinciaIdPorNombre.get("Formosa"), "Clorinda", -25.2847, -57.7196),
                new Localidad(null, provinciaIdPorNombre.get("Formosa"), "Pirané", -25.7333, -59.1089),

                new Localidad(null, provinciaIdPorNombre.get("Jujuy"), "San Salvador de Jujuy", -24.1858, -65.2995),
                new Localidad(null, provinciaIdPorNombre.get("Jujuy"), "Palpalá", -24.2586, -65.2116),
                new Localidad(null, provinciaIdPorNombre.get("Jujuy"), "Perico", -24.3816, -65.1126),

                new Localidad(null, provinciaIdPorNombre.get("La Pampa"), "Santa Rosa", -36.6202, -64.2906),
                new Localidad(null, provinciaIdPorNombre.get("La Pampa"), "General Pico", -35.6566, -63.7568),
                new Localidad(null, provinciaIdPorNombre.get("La Pampa"), "Toay", -36.6761, -64.3833),

                new Localidad(null, provinciaIdPorNombre.get("La Rioja"), "La Rioja Capital", -29.4128, -66.8558),
                new Localidad(null, provinciaIdPorNombre.get("La Rioja"), "Chilecito", -29.1627, -67.4977),
                new Localidad(null, provinciaIdPorNombre.get("La Rioja"), "Aimogasta", -28.5636, -66.9482),

                new Localidad(null, provinciaIdPorNombre.get("Mendoza"), "Mendoza Capital", -32.889458, -68.845839),
                new Localidad(null, provinciaIdPorNombre.get("Mendoza"), "Godoy Cruz", -32.92397, -68.85809),
                new Localidad(null, provinciaIdPorNombre.get("Mendoza"), "San Rafael", -34.61772, -68.33007),

                new Localidad(null, provinciaIdPorNombre.get("Misiones"), "Posadas", -27.3621, -55.9009),
                new Localidad(null, provinciaIdPorNombre.get("Misiones"), "Oberá", -27.4871, -55.1199),
                new Localidad(null, provinciaIdPorNombre.get("Misiones"), "Eldorado", -26.4095, -54.6418),

                new Localidad(null, provinciaIdPorNombre.get("Neuquén"), "Neuquén Capital", -38.9516, -68.0591),
                new Localidad(null, provinciaIdPorNombre.get("Neuquén"), "San Martín de los Andes", -40.1579, -71.3534),
                new Localidad(null, provinciaIdPorNombre.get("Neuquén"), "Cutral Có", -38.9395, -69.2306),

                new Localidad(null, provinciaIdPorNombre.get("Río Negro"), "Viedma", -40.8135, -62.9967),
                new Localidad(null, provinciaIdPorNombre.get("Río Negro"), "San Carlos de Bariloche", -41.1335, -71.3103),
                new Localidad(null, provinciaIdPorNombre.get("Río Negro"), "Cipolletti", -38.9339, -67.9901),

                new Localidad(null, provinciaIdPorNombre.get("Salta"), "Salta Capital", -24.7821, -65.4232),
                new Localidad(null, provinciaIdPorNombre.get("Salta"), "Tartagal", -22.516, -63.8069),
                new Localidad(null, provinciaIdPorNombre.get("Salta"), "Cafayate", -26.0732, -65.977),

                new Localidad(null, provinciaIdPorNombre.get("San Juan"), "San Juan Capital", -31.5375, -68.5364),
                new Localidad(null, provinciaIdPorNombre.get("San Juan"), "Caucete", -31.6515, -68.281),
                new Localidad(null, provinciaIdPorNombre.get("San Juan"), "Calingasta", -31.335, -69.3969),

                new Localidad(null, provinciaIdPorNombre.get("San Luis"), "San Luis Capital", -33.3017, -66.3378),
                new Localidad(null, provinciaIdPorNombre.get("San Luis"), "Villa Mercedes", -33.6757, -65.4573),
                new Localidad(null, provinciaIdPorNombre.get("San Luis"), "Merlo", -32.3446, -65.0139),

                new Localidad(null, provinciaIdPorNombre.get("Santa Cruz"), "Río Gallegos", -51.6226, -69.2181),
                new Localidad(null, provinciaIdPorNombre.get("Santa Cruz"), "Caleta Olivia", -46.4393, -67.5231),
                new Localidad(null, provinciaIdPorNombre.get("Santa Cruz"), "El Calafate", -50.3379, -72.2648),

                new Localidad(null, provinciaIdPorNombre.get("Santa Fe"), "Rosario", -32.944242, -60.650539),
                new Localidad(null, provinciaIdPorNombre.get("Santa Fe"), "Santa Fe Capital", -31.633333, -60.7),
                new Localidad(null, provinciaIdPorNombre.get("Santa Fe"), "Rafaela", -31.25033, -61.4867),

                new Localidad(null, provinciaIdPorNombre.get("Santiago del Estero"), "Santiago del Estero Capital", -27.7834, -64.2642),
                new Localidad(null, provinciaIdPorNombre.get("Santiago del Estero"), "La Banda", -27.7349, -64.2527),
                new Localidad(null, provinciaIdPorNombre.get("Santiago del Estero"), "Termas de Río Hondo", -27.4932, -64.8605),

                new Localidad(null, provinciaIdPorNombre.get("Tierra del Fuego"), "Ushuaia", -54.8019, -68.303),
                new Localidad(null, provinciaIdPorNombre.get("Tierra del Fuego"), "Río Grande", -53.7999, -67.699),
                new Localidad(null, provinciaIdPorNombre.get("Tierra del Fuego"), "Tolhuin", -54.509, -67.2009),

                new Localidad(null, provinciaIdPorNombre.get("Tucumán"), "San Miguel de Tucumán", -26.8083, -65.2176),
                new Localidad(null, provinciaIdPorNombre.get("Tucumán"), "Tafí Viejo", -26.732, -65.2592),
                new Localidad(null, provinciaIdPorNombre.get("Tucumán"), "Yerba Buena", -26.8162, -65.3169)
        );

        localidadRepositoryDB.saveAll(localidades);
        log.info("Catálogo de provincias y localidades inicializado ({} provincias, {} localidades).", provinciasGuardadas.size(), localidades.size());
    }
}
