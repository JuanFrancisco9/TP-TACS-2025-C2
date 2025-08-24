package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.dtos.UsuarioDto;
import org.utn.ba.tptacsg2.services.UsuarioService;

import java.util.List;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@RestController
public class UsuarioController {
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     *  Registra un usuario en el sistema.
     * @param usuarioDto Objeto que contiene la informacion del usuario a registrar.
     * @return ResponseEntity con el estado de la operacion y un mensaje descriptivo.
     * - 201: Usuario registrado correctamente.
     * - 400: Error del servidor con el mensaje de la excepcion.
     */
    @PostMapping("/user")
    public ResponseEntity<String> registrarUsuario(@RequestBody UsuarioDto usuarioDto) {
        // Si algun campo falta da un 400 por la anottation del dto notblack
        // Entonces si rompe aca es por un error interno
        try{
            usuarioService.registrar(usuarioDto);
        } catch (Exception e){
            return ResponseEntity.status(400).body("Error del servidor: " + e.getMessage());
        }
        return ResponseEntity.status(201).body("Usuario registrado correctamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public List<UsuarioDto> getUsuarios() {
        return usuarioService.getUsuarios();
    }
}
