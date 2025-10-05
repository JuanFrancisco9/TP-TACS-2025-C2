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
import org.utn.ba.tptacsg2.models.users.Rol;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.repositories.db.*; // Asume que tienes todos los repositorios definidos

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    public DatabaseSeeder(
            UsuarioRepositoryDB usuarioRepository,
            OrganizadorRepositoryDB organizadorRepository,
            ParticipanteRepositoryDB participanteRepository,
            EventoRepositoryDB eventoRepositoryDB,
            CategoriaRepositoryDB categoriaRepositoryDB,
            EstadoEventoRepositoryDB estadoEventoRepository,
            InscripcionRepositoryDB inscripcionRepository,
            EstadoInscripcionRepositoryDB estadoInscripcionRepositoryDB,
            PasswordEncoder passwordEncoder
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
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando la inicialización de la base de datos...");

        cleanUp();

        Usuario admin = new Usuario( "admin", "$argon2id$v=19$m=65536,t=4,p=1$Y6QXibp2pRk+u6XDSSX6Wg$vaFyKiCj6Tvl06OGHuJtPaw5+4iZDi4f2iN0jrsYYLs", Rol.ROLE_ADMIN);
        Usuario userPart = new Usuario( "usuario", "$argon2id$v=19$m=65536,t=4,p=1$hC1J7qKqgmkSUfl8kMdQow$wva2eKpy3Mw8/oJPvJw5JdPse+cEJ73EdmcT6uhcXmU", Rol.ROLE_USER);
        Usuario userOrg = new Usuario( "organizador", "$argon2id$v=19$m=65536,t=4,p=1$IDXLIGuWc88CLL+7VyhCOA$CXr5e1xeozTTolyjDn1PNX1cs9uHqXFbH6TrtDKOCtk", Rol.ROLE_ORGANIZER);

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

        Ubicacion ubicacion = new Ubicacion("34.6037", "58.3816", "CABA", "Av. 9 de Julio 1234");
        Precio precio = new Precio("ARS", 1500.0f);
        Categoria categoria = new Categoria("Conferencia");
        categoria = categoriaRepositoryDB.save(categoria);

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


        estadoEvento.setEvento(evento);
        estadoEventoRepository.save(estadoEvento);

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

    private void cleanUp() {
        inscripcionRepository.deleteAll();
        estadoInscripcionRepositoryDB.deleteAll();
        eventoRepositoryDB.deleteAll();
        categoriaRepositoryDB.deleteAll();
        estadoEventoRepository.deleteAll();
        participanteRepository.deleteAll();
        organizadorRepository.deleteAll();
        usuarioRepository.deleteAll();
        log.info("Colecciones limpiadas.");
    }
}
