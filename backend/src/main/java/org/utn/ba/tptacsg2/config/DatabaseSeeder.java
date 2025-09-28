package org.utn.ba.tptacsg2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

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
    private final EstadoEventoRepositoryDB estadoEventoRepository;
    private final InscripcionRepositoryDB inscripcionRepository;
    private final EstadoInscripcionRepositoryDB estadoInscripcionRepositoryDB;

    public DatabaseSeeder(
            UsuarioRepositoryDB usuarioRepository,
            OrganizadorRepositoryDB organizadorRepository,
            ParticipanteRepositoryDB participanteRepository,
            EventoRepositoryDB eventoRepositoryDB,
            EstadoEventoRepositoryDB estadoEventoRepository,
            InscripcionRepositoryDB inscripcionRepository,
            EstadoInscripcionRepositoryDB estadoInscripcionRepositoryDB
    ) {
        this.usuarioRepository = usuarioRepository;
        this.organizadorRepository = organizadorRepository;
        this.participanteRepository = participanteRepository;
        this.eventoRepositoryDB = eventoRepositoryDB;
        this.estadoEventoRepository = estadoEventoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.estadoInscripcionRepositoryDB = estadoInscripcionRepositoryDB;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando la inicialización de la base de datos...");

        // 1. Limpiar todas las colecciones para un inicio limpio
        cleanUp();

        // 2. Crear Usuarios, Organizadores y Participantes
        Usuario admin = new Usuario( "admin", "$argon2id$v=19$m=65536,t=4,p=1$Y6QXibp2pRk+u6XDSSX6Wg$vaFyKiCj6Tvl06OGHuJtPaw5+4iZDi4f2iN0jrsYYLs", Rol.ROLE_ADMIN);
        Usuario userPart = new Usuario( "usuario", "$argon2id$v=19$m=65536,t=4,p=1$hC1J7qKqgmkSUfl8kMdQow$wva2eKpy3Mw8/oJPvJw5JdPse+cEJ73EdmcT6uhcXmU", Rol.ROLE_USER);
        Usuario userOrg = new Usuario( "organizador", "$argon2id$v=19$m=65536,t=4,p=1$IDXLIGuWc88CLL+7VyhCOA$CXr5e1xeozTTolyjDn1PNX1cs9uHqXFbH6TrtDKOCtk", Rol.ROLE_ORGANIZER);

        List<Usuario> savedUsers = usuarioRepository.saveAll(Arrays.asList(userOrg, userPart, admin));

        Usuario savedUserOrg = savedUsers.stream()
                .filter(u -> u.username().equals("organizador"))
                .findFirst().orElseThrow();
        Usuario savedUserPart = savedUsers.stream()
                .filter(u -> u.username().equals("usuario"))
                .findFirst().orElseThrow();

        Organizador organizador = new Organizador(
                "Juan", "Perez", "12345678", savedUserOrg
        );
        Participante participante = new Participante(
                "Maria", "Gomez", "87654321", savedUserPart
        );

        // ✅ CORRECCIÓN 1: Capturar el ID del organizador y participante para usarlos en Evento e Inscripción.
        organizador = organizadorRepository.save(organizador);
        participante = participanteRepository.save(participante);

        log.info("Usuarios, Organizadores y Participantes creados.");


        // 3. Crear EstadoEvento y Evento
        EstadoEvento estadoEvento = new EstadoEvento(
                TipoEstadoEvento.CONFIRMADO,
                LocalDateTime.now()
        );
        // ✅ CORRECCIÓN 2: Capturar el ID de EstadoEvento. (Esto ya lo tenías bien)
        estadoEvento = estadoEventoRepository.save(estadoEvento);

        Ubicacion ubicacion = new Ubicacion("34.6037", "58.3816", "CABA", "Av. 9 de Julio 1234");
        Precio precio = new Precio("ARS", 1500.0f);
        Categoria categoria = new Categoria("Conferencia");

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
                organizador, // 👈 organizador tiene ID gracias a la Corrección 1
                estadoEvento, // 👈 estadoEvento tiene ID
                categoria,
                Arrays.asList("Java", "Spring", "Microservicios")
        );

        // ✅ CORRECCIÓN 3: Capturar el ID de Evento. (Esto ya lo tenías bien)
        evento = eventoRepositoryDB.save(evento);

        // Finalizar la referencia circular de EstadoEvento a Evento (ambos tienen ID).
        estadoEvento.setEvento(evento);
        estadoEventoRepository.save(estadoEvento);

        log.info("Evento y EstadoEvento creados.");


        // 4. Crear EstadoInscripcion e Inscripcion
        EstadoInscripcion estadoInscripcion = new EstadoInscripcion(
                "885558888",
                TipoEstadoInscripcion.ACEPTADA,
                LocalDateTime.now()
        );
        // ✅ CORRECCIÓN 4: Capturar el ID de EstadoInscripcion. (Esto ya lo tenías bien)
        estadoInscripcion = estadoInscripcionRepositoryDB.save(estadoInscripcion);

        Inscripcion inscripcion = new Inscripcion(
                participante, // 👈 participante tiene ID gracias a la Corrección 1
                LocalDateTime.now(),
                estadoInscripcion, // 👈 estadoInscripcion tiene ID
                evento // 👈 evento tiene ID
        );

        // ✅ CORRECCIÓN 5: Capturar el ID de Inscripcion. (Esto ya lo tenías bien)
        inscripcion = inscripcionRepository.save(inscripcion);

        // Finalizar la referencia circular de EstadoInscripcion a Inscripcion (ambos tienen ID).
        estadoInscripcion.setInscripcion(inscripcion);
        estadoInscripcionRepositoryDB.save(estadoInscripcion);

        log.info("Inscripción y EstadoInscripcion creados.");

        log.info("Base de datos inicializada con datos de ejemplo.");
    }

    private void cleanUp() {
        // Elimina todos los documentos de cada colección para evitar duplicados en cada inicio.
        inscripcionRepository.deleteAll();
        estadoInscripcionRepositoryDB.deleteAll();
        eventoRepositoryDB.deleteAll();
        estadoEventoRepository.deleteAll();
        participanteRepository.deleteAll();
        organizadorRepository.deleteAll();
        usuarioRepository.deleteAll();
        log.info("Colecciones limpiadas.");
    }
}