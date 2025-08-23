package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.dtos.UsuarioDto;
import org.utn.ba.tptacsg2.services.UsuarioService;

import java.util.List;

@RestController
public class UsuarioController {
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/user")
    public ResponseEntity<String> registrarUsuario(@RequestBody UsuarioDto usuarioDto) {
        // Si algun campo falta da un 400 por la anottation del dto notblack
        // Entonces si rompe aca es por un error interno
        try{
            usuarioService.registrar(usuarioDto);
        } catch (Exception e){
            return ResponseEntity.status(500).body("Error del servidor: " + e.getMessage());
        }
        return ResponseEntity.status(200).body("Usuario registrado correctamente");
    }

    @GetMapping("/user")
    public List<UsuarioDto> getUsuarios() {
        return usuarioService.getUsuarios();
    }
}
