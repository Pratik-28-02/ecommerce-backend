package com.ecommerce.service;

import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public Order createOrder(Long userId) {
        System.out.println("OrderService: Looking for user with ID: " + userId); // Debug log
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: ID " + userId));

        List<Cart> cartItems = cartRepository.findByUser_Id(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty for user ID: " + userId);
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setTotalAmount(BigDecimal.ZERO);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());

        // Save Order first to generate ID
        Order savedOrder = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (cartItem.getQuantity() > product.getStock()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            savedOrder.getOrderItems().add(orderItem);
            // Update stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        }
        savedOrder.setTotalAmount(totalAmount);
        Order finalOrder = orderRepository.save(savedOrder); // Save Order and OrderItems
        cartRepository.deleteByUser_Id(userId); // Delete cart after successful save
        return finalOrder;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}