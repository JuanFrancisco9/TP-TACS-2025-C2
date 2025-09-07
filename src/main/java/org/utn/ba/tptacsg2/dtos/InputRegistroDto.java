package org.utn.ba.tptacsg2.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InputRegistroDto {
    private Long id;
    @NotBlank(message = "Debe completar todos los campos")
    private String username;
    @NotBlank(message = "Debe completar todos los campos")
    private String password;
    @NotBlank(message = "Debe completar todos los campos")
    private String rol;
    @NotBlank(message = "Debe completar todos los campos")
    private String nombre;
    @NotBlank(message = "Debe completar todos los campos")
    private String apellido;
    @NotBlank(message = "Debe completar todos los campos")
    private String dni;

    public InputRegistroDto(Long id, String username, String password, String rol, String nombre, String apellido, String dni) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
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

    public @NotBlank(message = "Debe completar todos los campos") String getNombre() {
        return nombre;
    }

    public @NotBlank(message = "Debe completar todos los campos") String getApellido() {
        return apellido;
    }

    public @NotBlank(message = "Debe completar todos los campos") String getDni() {
        return dni;
    }
}
