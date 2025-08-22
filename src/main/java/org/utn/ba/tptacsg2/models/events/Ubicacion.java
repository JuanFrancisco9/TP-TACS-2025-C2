package org.utn.ba.tptacsg2.models.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion {
    private String latitud;
    private String longitud;
    private String direccion;
}
