package org.utn.ba.tptacsg2.models.users;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "usuarios")
public record Usuario(
        @Id
        String id,
        String username,
        String passwordHash,
        Rol rol
) {}

