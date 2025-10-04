package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.events.Categoria;
import org.utn.ba.tptacsg2.repositories.db.CategoriaRepositoryDB;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepositoryDB categoriaRepository;
    private final GeneradorIDService generadorIDService;

    @Autowired
    public CategoriaService(CategoriaRepositoryDB categoriaRepository, GeneradorIDService generadorIDService) {
        this.categoriaRepository = categoriaRepository;
        this.generadorIDService = generadorIDService;
    }

    public List<Categoria> getCategorias() {
        return this.categoriaRepository.findAll();
    }

    public void guardarCategoria(Categoria categoria) {
        this.categoriaRepository.save(categoria);
    }

    /**
     * Busca una categoría por su tipo (nombre). Si no existe, la crea y persiste.
     * @param nombreCategoria El nombre/tipo de la categoría
     * @return La categoría existente o recién creada
     */
    public Categoria obtenerOCrearCategoria(String nombreCategoria) {
        if (nombreCategoria == null || nombreCategoria.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }

        String nombreNormalizado = nombreCategoria.trim();

        // Verificar si existe primero
        if (categoriaRepository.existsByTipo(nombreNormalizado)) {
            // Si existe, buscarla y devolverla
            List<Categoria> categoriasEncontradas = categoriaRepository.findByTipo(nombreNormalizado);
            return categoriasEncontradas.getFirst();
        }

        // Si no existe, crear nueva categoría
        Categoria nuevaCategoria = new Categoria(generadorIDService.generarID(), nombreNormalizado);
        categoriaRepository.save(nuevaCategoria);
        return nuevaCategoria;
    }
}
