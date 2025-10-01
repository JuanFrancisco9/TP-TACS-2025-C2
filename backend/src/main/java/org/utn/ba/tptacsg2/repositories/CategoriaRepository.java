package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.Categoria;
import org.utn.ba.tptacsg2.repositories.db.CategoriaRepositoryDB;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoriaRepository {
    private final CategoriaRepositoryDB categoriaDB;

    @Autowired
    public CategoriaRepository(CategoriaRepositoryDB categoriaRepositoryDB) {
        this.categoriaDB = categoriaRepositoryDB;
    }

    public List<Categoria> getCategorias() {
        return categoriaDB.findAll();
    }

    public void guardarCategoria(Categoria categoria){
        if(!this.existeCategoria(categoria))
            categoriaDB.save(new Categoria(categoria.tipo().toLowerCase()));
    }

    private boolean existeCategoria(Categoria categoria){
        return categoriaDB.existsByTipoIsIgnoreCase(categoria.tipo().toLowerCase());
        //return this.getCategorias().stream().map(c -> c.tipo().toLowerCase()).toList().contains(categoria.tipo().toLowerCase());
    }

    @PostConstruct
    public void init(){
        this.guardarCategoria(new Categoria("Rock"));
        this.guardarCategoria(new Categoria("Animu"));
        this.guardarCategoria(new Categoria("lig o legen"));
        this.guardarCategoria(new Categoria("TACS BME (Best Materia Ever)"));
        this.guardarCategoria(new Categoria("Reinosa"));
    }
}
