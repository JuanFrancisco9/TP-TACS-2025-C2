package org.utn.ba.tptacsg2.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDto {
    private Long id;
    @NotBlank(message = "Debe completar todos los campos")
    private String username;
    @NotBlank(message = "Debe completar todos los campos")
    private String password;
    @NotBlank(message = "Debe completar todos los campos")
    private String rol;

    public UsuarioDto(Long id, String username, String password, String rol) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public @NotBlank(message = "Debe completar todos los campos") String getUsername() {
        return username;
    }

    public @NotBlank(message = "Debe completar todos los campos") String getRol() {
        return rol;
    }

    public @NotBlank(message = "Debe completar todos los campos") String getPassword() {
        return password;
    }
}
