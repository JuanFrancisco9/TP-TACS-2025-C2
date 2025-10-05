package org.utn.ba.tptacsg2.models.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("localidades")
public class Localidad {
    @Id
    private String id;
    private String provinciaId;
    private String nombre;
    private Double latitud;
    private Double longitud;
}
