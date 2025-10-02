package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.events.Evento;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;

import java.util.List;

public interface InscripcionRepositoryDB extends MongoRepository<Inscripcion, String> {
    List<Inscripcion> findAllByEvento(Evento evento);
    List<Inscripcion> findAllByEventoAndEstado_TipoEstado(Evento evento, TipoEstadoInscripcion estado);
    List<Inscripcion> findAllByParticipante_Id(String idParticipante);
}