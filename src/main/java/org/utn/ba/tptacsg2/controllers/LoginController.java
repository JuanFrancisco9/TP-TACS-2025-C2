package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.dtos.UsuarioDto;
import org.utn.ba.tptacsg2.models.users.Usuario;
import org.utn.ba.tptacsg2.services.UsuarioService;

@RestController
public class LoginController {
    private UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para loguear un usuario
     * @param usuario UsuarioDto con username y password
     * @return 200 si el login es exitoso, 401 si no lo es
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UsuarioDto usuario) {
        Usuario user;
        try {
            user = usuarioService.login(usuario);
        } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(200).build();
    }
}
