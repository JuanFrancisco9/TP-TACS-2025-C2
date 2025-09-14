package org.utn.ba.tptacsg2.models.actors;

import org.utn.ba.tptacsg2.models.users.Usuario;

public record Participante(
        String id,
        String nombre,
        String apellido,
        String dni,
        Usuario usuario
) {
}
