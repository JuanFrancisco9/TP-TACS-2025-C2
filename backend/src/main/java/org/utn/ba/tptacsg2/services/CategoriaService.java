package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.models.events.Categoria;
import org.utn.ba.tptacsg2.repositories.db.CategoriaRepositoryDB;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CategoriaService {
    private final CategoriaRepositoryDB categoriaRepository;
    private final GeneradorIDService generadorIDService;

    private static final Map<String, String> ICONOS_PREDEFINIDOS = Map.ofEntries(
            Map.entry("conferencia", "Event"),
            Map.entry("concierto", "MusicNote"),
            Map.entry("música", "MusicNote"),
            Map.entry("musica", "MusicNote"),
            Map.entry("deportes", "SportsEsports"),
            Map.entry("teatro", "TheaterComedy"),
            Map.entry("educación", "School"),
            Map.entry("educacion", "School"),
            Map.entry("entretenimiento", "LocalActivity"),
            Map.entry("turismo", "TravelExplore"),
            Map.entry("ciencia", "Science"),
            Map.entry("comunidad", "Diversity3")
    );

    @Autowired
    public CategoriaService(CategoriaRepositoryDB categoriaRepository, GeneradorIDService generadorIDService) {
        this.categoriaRepository = categoriaRepository;
        this.generadorIDService = generadorIDService;
    }

    private String iconoPorDefecto(String nombreCategoria) {
        if (nombreCategoria == null || nombreCategoria.isBlank()) {
            return null;
        }
        return ICONOS_PREDEFINIDOS.get(nombreCategoria.toLowerCase(Locale.ROOT));
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
        if (nombreCategoria == null) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        return obtenerOCrearCategoria(new Categoria(nombreCategoria));
    }

    public Categoria obtenerOCrearCategoria(Categoria categoriaData) {
        if (categoriaData == null || categoriaData.getTipo() == null || categoriaData.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }

        String nombreNormalizado = categoriaData.getTipo().trim();

        if (categoriaRepository.existsByTipo(nombreNormalizado)) {
            List<Categoria> categoriasEncontradas = categoriaRepository.findByTipo(nombreNormalizado);
            Categoria existente = categoriasEncontradas.getFirst();

            String nuevoIcono = categoriaData.getIcono();
            if (nuevoIcono == null || nuevoIcono.isBlank()) {
                nuevoIcono = iconoPorDefecto(nombreNormalizado);
            }

            if (nuevoIcono != null && (existente.getIcono() == null || !existente.getIcono().equals(nuevoIcono))) {
                existente.setIcono(nuevoIcono);
                categoriaRepository.save(existente);
            }
            return existente;
        }

        String icono = categoriaData.getIcono();
        if (icono == null || icono.isBlank()) {
            icono = iconoPorDefecto(nombreNormalizado);
        }

        Categoria nuevaCategoria = new Categoria(generadorIDService.generarID(), nombreNormalizado, icono);
        categoriaRepository.save(nuevaCategoria);
        return nuevaCategoria;
    }
}
