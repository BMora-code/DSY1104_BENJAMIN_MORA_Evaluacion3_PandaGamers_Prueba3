package com.example.backend.model;

public class OrderItem {

    private String productId;
    private Integer quantity;
    private String name;
    private double price;
    private double precioOriginal;
    private boolean tieneDescuentoDuoc;
    private String image;

    public OrderItem() {}

    public OrderItem(String productId, Integer quantity, String name, double price, String image) {
        this.productId = productId;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public OrderItem(String productId, Integer quantity, String name, double price, double precioOriginal, boolean tieneDescuentoDuoc, String image) {
        this.productId = productId;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.precioOriginal = precioOriginal;
        this.tieneDescuentoDuoc = tieneDescuentoDuoc;
        this.image = image;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrecioOriginal() {
        return precioOriginal;
    }

    public void setPrecioOriginal(double precioOriginal) {
        this.precioOriginal = precioOriginal;
    }

    public boolean isTieneDescuentoDuoc() {
        return tieneDescuentoDuoc;
    }

    public void setTieneDescuentoDuoc(boolean tieneDescuentoDuoc) {
        this.tieneDescuentoDuoc = tieneDescuentoDuoc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
