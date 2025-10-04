package org.utn.ba.tptacsg2.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.utn.ba.tptacsg2.models.events.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository {
    static private final List<Categoria> categorias =  new ArrayList<>();
    static private CategoriaRepository categoriaRepository;

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void guardarCategoria(Categoria categoria){
        if(!this.existeCategoria(categoria))
            categorias.add(new Categoria(categoria.getTipo().toLowerCase()));
    }

    private boolean existeCategoria(Categoria categoria){
        return this.getCategorias().stream().map(c -> c.getTipo().toLowerCase()).toList().contains(categoria.getTipo().toLowerCase());
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
