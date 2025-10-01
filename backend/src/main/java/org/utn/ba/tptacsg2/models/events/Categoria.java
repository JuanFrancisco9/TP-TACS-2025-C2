package org.utn.ba.tptacsg2.models.events;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public record Categoria (
    @Id
    String tipo
){}
