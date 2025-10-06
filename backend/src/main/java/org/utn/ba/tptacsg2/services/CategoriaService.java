package org.utn.ba.tptacsg2.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.CategoriaIconRuleDTO;
import org.utn.ba.tptacsg2.models.events.Categoria;
import org.utn.ba.tptacsg2.repositories.db.CategoriaRepositoryDB;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    private final CategoriaRepositoryDB categoriaRepository;
    private final GeneradorIDService generadorIDService;
    private final List<IconRule> iconRules;
    private static final Logger LOG = LoggerFactory.getLogger(CategoriaService.class);

    private static final String DEFAULT_ICON = "Category";
    private record IconRule(String icono, List<String> displayKeywords, List<String> normalizedKeywords) {
        boolean matches(String normalizedNombre) {
            if (normalizedNombre == null || normalizedNombre.isBlank()) {
                return false;
            }
            return normalizedKeywords.stream().anyMatch(normalizedNombre::contains);
        }

        CategoriaIconRuleDTO asDTO() {
            return new CategoriaIconRuleDTO(icono, displayKeywords);
        }
    }

    private record IconRuleFileEntry(String icono, List<String> keywords) {}

    @Autowired
    public CategoriaService(CategoriaRepositoryDB categoriaRepository, GeneradorIDService generadorIDService) {
        this.categoriaRepository = categoriaRepository;
        this.generadorIDService = generadorIDService;
        this.iconRules = cargarIconRules();
    }

    private String iconoPorDefecto(String nombreCategoria) {
        if (nombreCategoria == null || nombreCategoria.isBlank()) {
            return DEFAULT_ICON;
        }
        String normalized = normalizarClave(nombreCategoria);
        return iconRules.stream()
                .filter(rule -> rule.matches(normalized))
                .map(IconRule::icono)
                .findFirst()
                .orElse(DEFAULT_ICON);
    }

    public List<Categoria> getCategorias() {
        return this.categoriaRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Categoria::getTipo, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
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

        String tipoOriginal = colapsarEspacios(categoriaData.getTipo());
        String claveNormalizada = normalizarClave(tipoOriginal);

        Categoria existente = categoriaRepository.findAll().stream()
                .filter(cat -> Objects.equals(normalizarClave(cat.getTipo()), claveNormalizada))
                .findFirst()
                .orElse(null);

        if (existente != null) {
            String iconoExistente = existente.getIcono();
            String nuevoIcono = categoriaData.getIcono();
            if (nuevoIcono == null || nuevoIcono.isBlank()) {
                nuevoIcono = iconoPorDefecto(existente.getTipo());
            }

            if (iconoExistente == null || !iconoExistente.equals(nuevoIcono)) {
                existente.setIcono(nuevoIcono);
                categoriaRepository.save(existente);
            }
            return existente;
        }

        String tipoFormateado = formatearParaMostrar(tipoOriginal);
        String icono = categoriaData.getIcono();
        if (icono == null || icono.isBlank()) {
            icono = iconoPorDefecto(tipoFormateado);
        }

        Categoria nuevaCategoria = new Categoria(generadorIDService.generarID(), tipoFormateado, icono);
        categoriaRepository.save(nuevaCategoria);
        return nuevaCategoria;
    }

    public List<CategoriaIconRuleDTO> listarReglasIconos() {
        return iconRules.stream()
                .map(IconRule::asDTO)
                .toList();
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

    private String formatearParaMostrar(String valor) {
        String saneado = colapsarEspacios(valor).toLowerCase(Locale.ROOT);
        if (saneado.isEmpty()) {
            return saneado;
        }
        return Arrays.stream(saneado.split(" "))
                .filter(part -> !part.isBlank())
                .map(part -> part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1))
                .collect(Collectors.joining(" "));
    }

    private List<IconRule> cargarIconRules() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("category-icon-rules.json")) {
            if (inputStream == null) {
                LOG.warn("No se encontró category-icon-rules.json en el classpath. Se utilizarán reglas por defecto.");
                return reglasPorDefecto();
            }

            List<IconRuleFileEntry> fileEntries = mapper.readValue(inputStream, new TypeReference<>() {});
            if (fileEntries == null || fileEntries.isEmpty()) {
                LOG.warn("El archivo category-icon-rules.json está vacío. Se utilizarán reglas por defecto.");
                return reglasPorDefecto();
            }

            List<IconRule> reglas = new ArrayList<>();
            for (IconRuleFileEntry entry : fileEntries) {
                if (entry == null || entry.icono() == null || entry.icono().isBlank()) {
                    continue;
                }
                List<String> keywords = entry.keywords() == null
                        ? List.of()
                        : entry.keywords().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(keyword -> !keyword.isBlank())
                        .toList();

                if (keywords.isEmpty()) {
                    continue;
                }

                List<String> normalized = keywords.stream()
                        .map(this::normalizarClave)
                        .filter(keyword -> !keyword.isBlank())
                        .toList();

                if (normalized.isEmpty()) {
                    continue;
                }

                reglas.add(new IconRule(entry.icono().trim(), keywords, normalized));
            }

            if (reglas.isEmpty()) {
                LOG.warn("No se pudieron cargar reglas válidas desde category-icon-rules.json. Se utilizarán reglas por defecto.");
                return reglasPorDefecto();
            }

            LOG.info("Se cargaron {} reglas de iconos para categorías.", reglas.size());
            return List.copyOf(reglas);
        } catch (IOException e) {
            LOG.error("Error al leer category-icon-rules.json. Se utilizarán reglas por defecto.", e);
            return reglasPorDefecto();
        }
    }

    private List<IconRule> reglasPorDefecto() {
        return List.of(
                crearReglaFallback("Event", List.of("conferencia", "charla", "congreso", "seminario")),
                crearReglaFallback("MusicNote", List.of("musica", "concierto", "recital", "festival")),
                crearReglaFallback("SportsEsports", List.of("deporte", "futbol", "basquet", "sport")),
                crearReglaFallback("TheaterComedy", List.of("teatro", "obra", "comedia", "drama")),
                crearReglaFallback("School", List.of("educacion", "clase", "curso", "taller", "capacitacion"))
        );
    }

    private IconRule crearReglaFallback(String icono, List<String> keywords) {
        List<String> limpiadas = keywords.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(keyword -> !keyword.isBlank())
                .toList();
        List<String> normalizadas = limpiadas.stream()
                .map(this::normalizarClave)
                .filter(keyword -> !keyword.isBlank())
                .toList();
        return new IconRule(icono, limpiadas, normalizadas);
    }
}
