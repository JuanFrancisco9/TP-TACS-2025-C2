package org.utn.ba.tptacsg2.dtos;

import org.utn.ba.tptacsg2.models.users.Rol;

public record LoginResponseDto(
        String id,
        String username,
        Rol rol,
        String actorId
) {
}