package org.utn.ba.tptacsg2.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.Imagen;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagenRepository extends MongoRepository<Imagen, String> {
    
    List<Imagen> findByOwnerUserId(Long ownerUserId);
    
    Optional<Imagen> findByKey(String key);
    
    void deleteByKey(String key);
}
