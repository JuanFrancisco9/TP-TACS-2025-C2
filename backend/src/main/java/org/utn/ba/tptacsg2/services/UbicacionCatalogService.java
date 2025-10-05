package org.utn.ba.tptacsg2.services;

import org.springframework.stereotype.Service;
import org.utn.ba.tptacsg2.dtos.location.LocalidadDTO;
import org.utn.ba.tptacsg2.dtos.location.ProvinciaDTO;
import org.utn.ba.tptacsg2.models.location.Localidad;
import org.utn.ba.tptacsg2.models.location.Provincia;
import org.utn.ba.tptacsg2.repositories.db.LocalidadRepositoryDB;
import org.utn.ba.tptacsg2.repositories.db.ProvinciaRepositoryDB;

import java.util.List;
import java.util.Optional;

@Service
public class UbicacionCatalogService {

    private final ProvinciaRepositoryDB provinciaRepository;
    private final LocalidadRepositoryDB localidadRepository;

    public UbicacionCatalogService(ProvinciaRepositoryDB provinciaRepository, LocalidadRepositoryDB localidadRepository) {
        this.provinciaRepository = provinciaRepository;
        this.localidadRepository = localidadRepository;
    }

    public List<ProvinciaDTO> listarProvincias() {
        return provinciaRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()))
                .map(provincia -> new ProvinciaDTO(provincia.getId(), provincia.getNombre()))
                .toList();
    }

    public List<LocalidadDTO> listarLocalidadesPorProvincia(String provinciaId) {
        return localidadRepository.findByProvinciaIdOrderByNombreAsc(provinciaId)
                .stream()
                .map(localidad -> new LocalidadDTO(
                        localidad.getId(),
                        localidad.getProvinciaId(),
                        localidad.getNombre(),
                        localidad.getLatitud(),
                        localidad.getLongitud()
                ))
                .toList();
    }

    public Optional<Provincia> buscarProvinciaPorNombre(String nombre) {
        if (nombre == null) {
            return Optional.empty();
        }
        return provinciaRepository.findByNombreIgnoreCase(nombre.trim());
    }

    public Optional<Localidad> buscarLocalidadPorNombreYProvincia(String localidadNombre, String provinciaId) {
        if (localidadNombre == null || provinciaId == null) {
            return Optional.empty();
        }
        return localidadRepository.findByProvinciaIdAndNombreIgnoreCase(provinciaId, localidadNombre.trim());
    }
}
