package com.example.backend.service;

import com.example.backend.dto.OrderRequest;
import com.example.backend.model.Order;
import com.example.backend.model.PaymentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {

    @Autowired
    private OrderService orderService;

    // Almacenamiento temporal de sesiones de pago (en producción usar Redis o BD)
    private final Map<String, PaymentSession> paymentSessions = new ConcurrentHashMap<>();

    public Map<String, String> iniciarPago(String userId, OrderRequest orderRequest) {
        // Generar token único
        String token = UUID.randomUUID().toString();

        // Crear sesión de pago
        PaymentSession session = new PaymentSession(token, userId, orderRequest);
        paymentSessions.put(token, session);

        // Limpiar sesiones expiradas
        cleanupExpiredSessions();

        return Map.of(
            "url", "/webpay/simulator",
            "token", token
        );
    }

    public Order confirmarPago(String token, String status) {
        PaymentSession session = paymentSessions.get(token);

        if (session == null) {
            throw new RuntimeException("Sesión de pago no encontrada o expirada");
        }

        if (session.isExpired()) {
            paymentSessions.remove(token);
            throw new RuntimeException("Sesión de pago expirada");
        }

        // Procesar el pago según el status
        Order order;
        if ("AUTHORIZED".equals(status)) {
            // Pago aprobado - crear orden completada
            order = orderService.processOrderAndPayment(session.getUserId(), session.getOrderRequest());
        } else {
            // Pago rechazado - crear orden cancelada
            order = orderService.createCancelledOrder(session.getUserId(), session.getOrderRequest());
        }

        // Limpiar sesión
        paymentSessions.remove(token);

        return order;
    }

    private void cleanupExpiredSessions() {
        paymentSessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}