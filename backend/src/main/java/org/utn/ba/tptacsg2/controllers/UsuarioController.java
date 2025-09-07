package org.utn.ba.tptacsg2.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.dtos.InputRegistroDto;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.services.UsuarioService;

import java.util.List;

@RestController
public class UsuarioController {
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     *  Registra un usuario en el sistema.
     * @param inputRegistroDto Objeto que contiene la informacion del usuario a registrar.
     * @return ResponseEntity con el estado de la operacion y un mensaje descriptivo.
     * - 201: Usuario registrado correctamente.
     * - 400: Error del servidor con el mensaje de la excepcion.
     */
    @PostMapping("/user")
    public ResponseEntity<String> registrarUsuario(@Valid @RequestBody InputRegistroDto inputRegistroDto) {
        // Si algun campo falta da un 400 por la anottation del dto notblack
        // Entonces si rompe aca es por un error interno
        try{
            usuarioService.registrar(inputRegistroDto);
        } catch (Exception e){
            return ResponseEntity.status(400).body("Error del servidor: " + e.getMessage());
        }
        return ResponseEntity.status(201).body("Usuario registrado correctamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public List<Usuario> getUsuarios() {
        return usuarioService.getUsuarios();
    }
}
