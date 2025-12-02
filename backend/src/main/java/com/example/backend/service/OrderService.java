package com.example.backend.service;

import com.example.backend.dto.OrderRequest;
import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.model.Product;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order processOrderAndPayment(String userId, OrderRequest orderRequest) {
        List<OrderItem> items = orderRequest.getItems();

        // First, process the payment
        boolean paymentSuccess = processPayment(orderRequest);

        // Create order with appropriate status based on payment result
        Order o = new Order();
        o.setUserId(userId);
        o.setItems(items);
        o.setTotal(orderRequest.getTotal());
        o.setSubtotal(orderRequest.getSubtotal());
        o.setDescuentoDuoc(orderRequest.getDescuentoDuoc());
        o.setIva(orderRequest.getIva());
        o.setShippingCost(orderRequest.getShippingCost());
        o.setDeliveryOption(orderRequest.getDeliveryOption());
        o.setShippingInfo(orderRequest.getShippingInfo());

        if (paymentSuccess) {
            // Payment successful - reduce stock and mark as completed
            for (OrderItem it : items) {
                Product p = productRepository.findById(it.getProductId()).orElse(null);
                if (p != null) {
                    int newStock = p.getStock() - it.getQuantity();
                    if (newStock < 0) {
                        throw new RuntimeException("Stock insuficiente para producto: " + p.getName());
                    }
                    p.setStock(newStock);
                    productRepository.save(p);
                }
            }
            o.setStatus("COMPLETED");
        } else {
            // Payment failed - don't reduce stock, mark as cancelled
            o.setStatus("CANCELLED");
        }

        return orderRepository.save(o);
    }

    private boolean processPayment(OrderRequest orderRequest) {
        // Simulate payment processing (same logic as PaymentController)
        java.util.Random random = new java.util.Random();
        int scenario = random.nextInt(10); // 0-9

        // 80% success rate, 20% failure rate
        return scenario >= 2; // success if scenario >= 2 (8 out of 10 scenarios)
    }

    public List<Order> findByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order getById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order updateStatus(String id, String status) {
        Order order = getById(id);
        if (order != null) {
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    public void delete(String id) {
        Order order = getById(id);
        if (order != null) {
            // Restaurar stock de los productos
            for (OrderItem item : order.getItems()) {
                Product p = productRepository.findById(item.getProductId()).orElse(null);
                if (p != null) {
                    p.setStock(p.getStock() + item.getQuantity());
                    productRepository.save(p);
                }
            }
        }
        orderRepository.deleteById(id);
    }

    public Order createCancelledOrder(String userId, OrderRequest orderRequest) {
        List<OrderItem> items = orderRequest.getItems();

        // Create cancelled order without reducing stock
        Order o = new Order();
        o.setUserId(userId);
        o.setItems(items);
        o.setTotal(orderRequest.getTotal());
        o.setSubtotal(orderRequest.getSubtotal());
        o.setDescuentoDuoc(orderRequest.getDescuentoDuoc());
        o.setIva(orderRequest.getIva());
        o.setShippingCost(orderRequest.getShippingCost());
        o.setDeliveryOption(orderRequest.getDeliveryOption());
        o.setShippingInfo(orderRequest.getShippingInfo());
        o.setStatus("CANCELLED");

        return orderRepository.save(o);
    }
}
