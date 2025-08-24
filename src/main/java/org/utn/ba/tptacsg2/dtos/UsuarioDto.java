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
}
