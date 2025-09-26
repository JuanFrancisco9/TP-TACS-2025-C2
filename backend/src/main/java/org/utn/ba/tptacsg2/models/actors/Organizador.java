package org.utn.ba.tptacsg2.models.actors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.utn.ba.tptacsg2.models.users.Usuario;



@Document(collection = "organizadores")
public record Organizador (
     @Id
     String id,
     String nombre,
     String apellido,
     String dni,
     @DBRef
     Usuario usuario
){}
