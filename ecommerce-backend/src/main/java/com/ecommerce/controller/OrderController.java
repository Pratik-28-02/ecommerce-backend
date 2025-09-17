package com.ecommerce.controller;

import com.ecommerce.dto.OrderRequest;
import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(Principal principal) {
        String userIdStr = principal.getName();
        System.out.println("OrderController: JWT User ID: " + userIdStr); // Debug log
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid JWT: User ID not found");
        }
        Long userId;
        try {
            userId = Long.parseLong(userIdStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid JWT: User ID must be a number, found: " + userIdStr);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: ID " + userId));
        System.out.println("OrderController: Found User ID: " + user.getId()); // Debug log
        return orderService.createOrder(user.getId());
    }

    @GetMapping
    public List<Order> getUserOrders(Principal principal) {
        String userIdStr = principal.getName();
        System.out.println("OrderController: JWT User ID for getUserOrders: " + userIdStr); // Debug log
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid JWT: User ID not found");
        }
        Long userId;
        try {
            userId = Long.parseLong(userIdStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid JWT: User ID must be a number, found: " + userIdStr);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: ID " + userId));
        return orderService.getUserOrders(user);
    }

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}