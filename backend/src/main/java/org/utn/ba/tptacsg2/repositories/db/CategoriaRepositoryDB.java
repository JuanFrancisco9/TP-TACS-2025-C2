package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.Categoria;

import java.util.List;

@Repository
public interface CategoriaRepositoryDB extends MongoRepository<Categoria, String> {

    List<Categoria> findByTipo(String tipo);
    boolean existsByTipo(String tipo);



}
