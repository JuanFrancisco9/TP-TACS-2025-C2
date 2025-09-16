package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.Categoria;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoriaRepository {
    static private final List<Categoria> categorias =  new ArrayList<>();
    static private CategoriaRepository categoriaRepository;

    public CategoriaRepository() { }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void guardarCategoria(Categoria categoria){
        categorias.add(categoria);
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
