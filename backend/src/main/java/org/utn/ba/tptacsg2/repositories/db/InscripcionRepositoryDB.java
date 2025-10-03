package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.actors.Participante;
import org.utn.ba.tptacsg2.models.inscriptions.Inscripcion;
import org.utn.ba.tptacsg2.models.inscriptions.TipoEstadoInscripcion;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepositoryDB extends MongoRepository<Inscripcion, String> {

    List<Inscripcion> findByEvento_Id(String eventoId);

    List<Inscripcion> findByParticipante_Id(String participanteId);

    @Aggregation(pipeline = {
            "{ $match: { 'evento.$id': ?0 } }",
            "{ $lookup: { from: 'estadoinscripciones', let: { estId: '$estado.$id' }, " +
                    "  pipeline: [ { $match: { $expr: { $eq: ['$_id', '$$estId'] } } } ], as: 'est' } }",
            "{ $unwind: '$est' }",
            "{ $match: { 'est.tipoEstado': ?1 } }",
            "{ $sort: { 'fechaRegistro': 1 } }"
    })
    List<Inscripcion> findWaitlistByEventoOrderByFechaAsc(
            String eventoId,
            TipoEstadoInscripcion tipo
    );

    // La “primera” de la waitlist
    @Aggregation(pipeline = {
            "{ $lookup: { " +
                    "   from: 'estadoinscripciones', " +
                    "   let: { estId: '$estado.$id' }, " +
                    "   pipeline: [ { $match: { $expr: { $eq: ['$_id', '$$estId'] } } } ], " +
                    "   as: 'est' } }",
            "{ $unwind: '$est' }",
            "{ $match: { 'evento.$id': ?0, 'est.tipoEstado': ?1 } }",
            "{ $sort: { 'fechaRegistro': 1 } }",
            "{ $limit: 1 }"
    })
    Optional<Inscripcion> findFirstInWaitlistByEventoAndTipoEstado(String eventoId,
                                                                   TipoEstadoInscripcion tipo);

    @Aggregation(pipeline = {
            "{ $match: { 'evento.$id': ?0 } }",
            "{ $lookup: { from: 'estadoinscripciones', localField: 'estado.$id', foreignField: '_id', as: 'estadoDoc' } }",
            "{ $unwind: '$estadoDoc' }",
            "{ $match: { 'estadoDoc.tipoEstado': 'ACEPTADA' } }",
            "{ $lookup: { from: 'participantes', localField: 'participante.$id', foreignField: '_id', as: 'part' } }",
            "{ $unwind: '$part' }",
            "{ $replaceWith: '$part' }"
    })
    List<Participante> findParticipantesAceptadosPorEvento(String eventoId);
}