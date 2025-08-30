package org.utn.ba.tptacsg2.models.users;

public record Usuario(
        Long id,
        String username,
        String passwordHash,
        Rol rol
) {
    @Override
    public Long id() {
        return id;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public String passwordHash() {
        return passwordHash;
    }

    @Override
    public Rol rol() {
        return rol;
    }
}

