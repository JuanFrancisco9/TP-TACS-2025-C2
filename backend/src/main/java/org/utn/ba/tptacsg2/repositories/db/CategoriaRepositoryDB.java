package org.utn.ba.tptacsg2.repositories.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.utn.ba.tptacsg2.models.events.Categoria;

public interface CategoriaRepositoryDB extends MongoRepository<Categoria, String> {
    boolean existsByTipoIsIgnoreCase(String lowerCase);
}
