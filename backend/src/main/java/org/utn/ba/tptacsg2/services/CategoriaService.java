package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.CategoriaIconRuleDTO;
import org.utn.ba.tptacsg2.models.events.Categoria;
import org.utn.ba.tptacsg2.repositories.db.CategoriaRepositoryDB;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    private final CategoriaRepositoryDB categoriaRepository;
    private static final String DEFAULT_ICON = "Category";

    @Autowired
    public CategoriaService(CategoriaRepositoryDB categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    private String iconoPorDefecto(String nombreCategoria) {
        if (nombreCategoria == null || nombreCategoria.isBlank()) {
            return DEFAULT_ICON;
        }
        return DEFAULT_ICON;
    }

    public List<Categoria> getCategorias() {
        return this.categoriaRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Categoria::getTipo, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Categoria obtenerCategoriaExistente(Categoria categoriaData) {
        if (categoriaData == null || categoriaData.getTipo() == null || categoriaData.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }

        String tipoOriginal = colapsarEspacios(categoriaData.getTipo());
        String claveNormalizada = normalizarClave(tipoOriginal);

        return categoriaRepository.findAll().stream()
                .filter(cat -> Objects.equals(normalizarClave(cat.getTipo()), claveNormalizada))
                .findFirst()
                .map(cat -> {
                    if (cat.getIcono() == null || cat.getIcono().isBlank()) {
                        cat.setIcono(iconoPorDefecto(cat.getTipo()));
                        categoriaRepository.save(cat);
                    }
                    return cat;
                })
                .orElseThrow(() -> new IllegalArgumentException("La categoría especificada no existe"));
    }

    public List<CategoriaIconRuleDTO> listarReglasIconos() {
        return List.of();
    }

    private String normalizarClave(String valor) {
        if (valor == null) {
            return "";
        }
        String trimmed = colapsarEspacios(valor).toLowerCase(Locale.ROOT);
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    private String colapsarEspacios(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.trim().replaceAll("\\s+", " ");
    }

}
