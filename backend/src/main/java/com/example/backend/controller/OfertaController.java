package com.example.backend.controller;

import com.example.backend.model.Oferta;
import com.example.backend.service.OfertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ofertas")
public class OfertaController {

    @Autowired
    private OfertaService ofertaService;

    @Operation(summary = "Listar ofertas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ofertas")
    })
    @GetMapping
    public ResponseEntity<List<Oferta>> list() {
        return ResponseEntity.ok(ofertaService.findAll());
    }

    @Operation(summary = "Crear oferta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Oferta creada")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Oferta> create(@RequestBody Oferta oferta) {
        return ResponseEntity.ok(ofertaService.create(oferta));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Oferta> update(@PathVariable String id, @RequestBody Oferta oferta) {
        return ResponseEntity.ok(ofertaService.update(id, oferta));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable String id) {
        ofertaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}