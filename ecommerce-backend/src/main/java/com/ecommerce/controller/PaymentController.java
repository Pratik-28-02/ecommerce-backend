package com.ecommerce.controller;

import com.ecommerce.dto.PaymentVerificationRequest;
import com.ecommerce.model.Payment;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment(@RequestParam Long orderId, Principal principal) throws RazorpayException {
        String userIdStr = principal.getName();
        System.out.println("PaymentController: Creating payment for orderId: " + orderId + ", User ID: " + userIdStr); // Debug log
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
        System.out.println("PaymentController: Found User ID: " + user.getId()); // Debug log
        return paymentService.createPayment(orderId, userId); // Pass userId for validation
    }

    @PostMapping("/verify")
    public Payment verifyPayment(@RequestBody PaymentVerificationRequest request) throws RazorpayException {
        System.out.println("PaymentController: Verifying payment for razorpayOrderId: " + request.getRazorpayOrderId()); // Debug log
        return paymentService.verifyPayment(request.getRazorpayOrderId(), request.getPaymentId(), request.getSignature());
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) {
        System.out.println("PaymentController: Fetching payment for ID: " + id); // Debug log
        return paymentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }
}