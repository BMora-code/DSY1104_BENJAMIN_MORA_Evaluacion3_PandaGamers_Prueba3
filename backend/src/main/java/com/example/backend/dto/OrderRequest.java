package com.example.backend.dto;

import com.example.backend.model.OrderItem;
import com.example.backend.model.ShippingInfo;
import java.util.List;

public class OrderRequest {
    private List<OrderItem> items;
    private ShippingInfo shippingInfo;
    private String deliveryOption;
    private double subtotal;
    private double descuentoDuoc;
    private double iva;
    private double shippingCost;
    private double total;

    // Payment information
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardName;

    public OrderRequest() {}

    // Getters and setters
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public ShippingInfo getShippingInfo() { return shippingInfo; }
    public void setShippingInfo(ShippingInfo shippingInfo) { this.shippingInfo = shippingInfo; }

    public String getDeliveryOption() { return deliveryOption; }
    public void setDeliveryOption(String deliveryOption) { this.deliveryOption = deliveryOption; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDescuentoDuoc() { return descuentoDuoc; }
    public void setDescuentoDuoc(double descuentoDuoc) { this.descuentoDuoc = descuentoDuoc; }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }

    public double getShippingCost() { return shippingCost; }
    public void setShippingCost(double shippingCost) { this.shippingCost = shippingCost; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }
}