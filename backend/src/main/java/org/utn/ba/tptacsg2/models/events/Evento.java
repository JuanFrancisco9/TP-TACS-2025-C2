package org.utn.ba.tptacsg2.models.events;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.utn.ba.tptacsg2.models.actors.Organizador;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "eventos")
public record Evento (
     @Id
     String id,
     String titulo,
     String descripcion,
     LocalDateTime fecha,
     String horaInicio,
     Float duracion,
     Ubicacion ubicacion,
     Integer cupoMaximo,
     Integer cupoMinimo,
     Precio precio,
     @DBRef
     Organizador organizador,
     @JsonManagedReference
     @DBRef
     EstadoEvento estado,
     @DBRef
     Categoria categoria,
     List<String> etiquetas
) {
}
