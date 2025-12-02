package com.example.backend.controller;

import com.example.backend.model.Review;
import com.example.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "Listar reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reviews")
    })
    @GetMapping
    public ResponseEntity<List<Review>> list() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    @Operation(summary = "Obtener reviews por producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews del producto")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getByProductId(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.findByProductId(productId));
    }

    @Operation(summary = "Crear review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review creada")
    })
    @PostMapping
    public ResponseEntity<Review> create(@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.create(review));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable String id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}