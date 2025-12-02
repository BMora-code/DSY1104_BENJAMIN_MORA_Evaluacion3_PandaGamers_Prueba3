package com.example.backend.controller;

import com.example.backend.dto.OrderRequest;
import com.example.backend.model.Order;
import com.example.backend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "Iniciar pago Webpay")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago iniciado correctamente")
    })
    @PostMapping("/pago/iniciar")
    public ResponseEntity<Map<String, String>> iniciarPago(@RequestBody OrderRequest orderRequest, Authentication auth) {
        String userId = auth.getName();
        Map<String, String> response = paymentService.iniciarPago(userId, orderRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Confirmar pago Webpay")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago confirmado"),
            @ApiResponse(responseCode = "400", description = "Error en confirmación")
    })
    @PostMapping("/pago/confirmar")
    public ResponseEntity<?> confirmarPago(@RequestBody Map<String, String> confirmData) {
        try {
            String token = confirmData.get("token");
            String status = confirmData.get("status");

            if (token == null || status == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token y status son requeridos"));
            }

            Order order = paymentService.confirmarPago(token, status);

            if ("AUTHORIZED".equals(status)) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "orderId", order.getId(),
                    "redirect", "/checkout/success/" + order.getId()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "status", "failed",
                    "orderId", order.getId(),
                    "redirect", "/checkout/error"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Mantener endpoint anterior para compatibilidad
    @PostMapping("/payments/create")
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, Object> paymentData) {
        // Simular diferentes escenarios de pago
        int scenario = (int) (Math.random() * 10); // 0-9

        if (scenario < 1) { // 10% de probabilidad de fallo por fondos insuficientes
            Map<String, String> response = Map.of(
                "status", "failed",
                "message", "Fondos insuficientes en la tarjeta"
            );
            return ResponseEntity.badRequest().body(response);
        } else if (scenario < 2) { // 10% de probabilidad de fallo por tarjeta rechazada
            Map<String, String> response = Map.of(
                "status", "failed",
                "message", "Tarjeta rechazada por el banco emisor"
            );
            return ResponseEntity.badRequest().body(response);
        } else { // 80% de probabilidad de éxito
            Map<String, String> response = Map.of(
                "status", "success",
                "message", "Pago procesado exitosamente"
            );
            return ResponseEntity.ok(response);
        }
    }
}
