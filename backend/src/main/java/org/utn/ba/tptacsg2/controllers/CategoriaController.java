package org.utn.ba.tptacsg2.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.utn.ba.tptacsg2.dtos.CategoriaIconRuleDTO;
import org.utn.ba.tptacsg2.models.events.Categoria;
import org.utn.ba.tptacsg2.services.CategoriaService;

import java.util.List;

@PreAuthorize("hasAnyRole('USER','ADMIN','ORGANIZER')")
@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping()
    public ResponseEntity<List<Categoria>> getCategorias() {
        List<Categoria> categorias;
        try{
            categorias = categoriaService.getCategorias();
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/iconos")
    public ResponseEntity<List<CategoriaIconRuleDTO>> getReglasIconos() {
        return ResponseEntity.ok(categoriaService.listarReglasIconos());
    }

    @PostMapping()
    public ResponseEntity<Categoria> agregarCategoria(@RequestBody Categoria categoria) {
        try {
            Categoria almacenada = categoriaService.obtenerOCrearCategoria(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(almacenada);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
