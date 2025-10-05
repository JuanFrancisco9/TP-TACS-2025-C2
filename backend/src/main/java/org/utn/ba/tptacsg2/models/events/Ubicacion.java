package org.utn.ba.tptacsg2.models.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.PersistenceCreator;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Ubicacion(
        String latitud,
        String longitud,
        String provincia,
        String localidad,
        String direccion,
        boolean esVirtual,
        String enlaceVirtual
) {
    public static Ubicacion presencial(String latitud,
                                       String longitud,
                                       String provincia,
                                       String localidad,
                                       String direccion) {
        return new Ubicacion(latitud, longitud, provincia, localidad, direccion, false, null);
    }

    public static Ubicacion virtual(String enlaceVirtual) {
        return new Ubicacion(null, null, null, null, null, true, enlaceVirtual);
    }

    @PersistenceCreator
    public static Ubicacion fromPersistence(
            String latitud,
            String longitud,
            String provincia,
            String localidad,
            String direccion,
            Boolean esVirtual,
            String enlaceVirtual
    ) {
        return new Ubicacion(
                latitud,
                longitud,
                provincia,
                localidad,
                direccion,
                Boolean.TRUE.equals(esVirtual),
                enlaceVirtual
        );
    }
}
