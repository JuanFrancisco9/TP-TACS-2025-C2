package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.Evento;

import java.util.Collection;
import java.util.List;

@Repository
public interface EventoRepositoryDB extends MongoRepository<Evento, String> {

    List<Evento> findByOrganizador_Id(String idOrganizador);

    @Aggregation(pipeline = {
            // Resolver el DBRef organizador
            "{ $lookup: { " +
                    "   from: 'organizadores', " +
                    "   let: { orgId: '$organizador.$id' }, " +
                    "   pipeline: [ { $match: { $expr: { $eq: ['$_id', '$$orgId'] } } } ], " +
                    "   as: 'org' } }",
            "{ $unwind: '$org' }",
            // Filtrar por el DBRef usuario dentro del organizador
            "{ $match: { 'org.usuario.$id': ?0 } }"
    })
    List<Evento> findByUsuarioIdDelOrganizador(String usuarioId);

    List<Evento> findByCategoria_Id(String categoriaId); // { 'categoria.$id': ... }
    List<Evento> findByEstado_Id(String estadoId);       // { 'estado.$id': ... }
    List<Evento> findByEtiquetasIn(Collection<String> etiquetas);
}



