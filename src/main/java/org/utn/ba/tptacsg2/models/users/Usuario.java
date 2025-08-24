package org.utn.ba.tptacsg2.models.users;

import lombok.Data;

public record Usuario(
        Long id,
        String username,
        String passwordHash,
        Rol rol
) {}

