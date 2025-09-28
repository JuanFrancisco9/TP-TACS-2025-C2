package org.utn.ba.tptacsg2.models.users;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public record Usuario(
        @Id
        String id,
        String username,
        String passwordHash,
        Rol rol
) {
    public Usuario(String username, String passwordHash, Rol rol) {
        this(null, username, passwordHash, rol);
    }
}

