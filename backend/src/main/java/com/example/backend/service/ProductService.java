package com.example.backend.service;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product create(Product p) {
        return productRepository.save(p);
    }

    public Product getById(String id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product update(String id, Product p) {
        Product existing = getById(id);
        existing.setName(p.getName());
        existing.setDescription(p.getDescription());
        existing.setPrice(p.getPrice());
        existing.setStock(p.getStock());
        existing.setImageUrl(p.getImageUrl());
        existing.setCategory(p.getCategory());
        return productRepository.save(existing);
    }

    public Product updateStock(String id, Integer newStock) {
        Product producto = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setStock(newStock);

        return productRepository.save(producto);
    }

    public void delete(String id) {
        productRepository.deleteById(id);
    }
}
