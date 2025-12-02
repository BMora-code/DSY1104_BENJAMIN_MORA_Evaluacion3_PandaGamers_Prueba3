package com.example.backend.model;

import com.example.backend.dto.OrderRequest;
import java.time.Instant;

public class PaymentSession {
    private String token;
    private String userId;
    private OrderRequest orderRequest;
    private Instant createdAt;
    private Instant expiresAt;

    public PaymentSession() {}

    public PaymentSession(String token, String userId, OrderRequest orderRequest) {
        this.token = token;
        this.userId = userId;
        this.orderRequest = orderRequest;
        this.createdAt = Instant.now();
        this.expiresAt = Instant.now().plusSeconds(300); // 5 minutos
    }

    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public OrderRequest getOrderRequest() { return orderRequest; }
    public void setOrderRequest(OrderRequest orderRequest) { this.orderRequest = orderRequest; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}