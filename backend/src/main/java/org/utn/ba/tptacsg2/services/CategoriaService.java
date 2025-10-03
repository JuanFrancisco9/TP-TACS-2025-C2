package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.events.Categoria;
import org.utn.ba.tptacsg2.repositories.db.CategoriaRepositoryDB;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepositoryDB categoriaRepository;

    @Autowired
    public CategoriaService(CategoriaRepositoryDB categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> getCategorias() {
        return this.categoriaRepository.findAll();
    }

    public void guardarCategoria(Categoria categoria) {
        this.categoriaRepository.save(categoria);
    }
}
