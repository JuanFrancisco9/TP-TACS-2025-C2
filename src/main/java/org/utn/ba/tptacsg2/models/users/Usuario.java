package org.utn.ba.tptacsg2.models.users;

import lombok.Data;

@Data
public class Usuario {
    private Long id;
    private String username;
    private String passwordHash;
    private Rol rol;
}
