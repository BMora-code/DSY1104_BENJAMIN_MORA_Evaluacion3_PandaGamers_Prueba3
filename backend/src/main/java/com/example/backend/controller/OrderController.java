package com.example.backend.controller;

import com.example.backend.dto.OrderRequest;
import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "Crear orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden creada")
    })
    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderRequest orderRequest, Authentication auth) {
        String userId = auth.getName(); // username como userId
        Order order = orderService.processOrderAndPayment(userId, orderRequest);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Listar órdenes del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de órdenes")
    })
    @GetMapping
    public ResponseEntity<List<Order>> list(Authentication auth) {
        String userId = auth.getName();
        List<Order> orders = orderService.findByUser(userId);
        // Debug: also return all orders for admin
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            List<Order> allOrders = orderService.findAll();
            System.out.println("DEBUG: Total orders in DB: " + allOrders.size());
            return ResponseEntity.ok(allOrders);
        }
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Obtener orden por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable String id) {
        Order order = orderService.getById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Orden eliminada")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable String id, @RequestBody java.util.Map<String, String> statusUpdate, Authentication auth) {
        Order order = orderService.getById(id);
        if (order != null) {
            // Allow if user owns the order or is admin
            boolean isOwner = order.getUserId().equals(auth.getName());
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isOwner || isAdmin) {
                Order updatedOrder = orderService.updateStatus(id, statusUpdate.get("status"));
                return ResponseEntity.ok(updatedOrder);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, Authentication auth) {
        Order order = orderService.getById(id);
        if (order != null) {
            // Allow if user owns the order or is admin
            boolean isOwner = order.getUserId().equals(auth.getName());
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isOwner || isAdmin) {
                orderService.delete(id);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}
