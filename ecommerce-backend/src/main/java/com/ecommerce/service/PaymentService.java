package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.model.Payment;
import com.ecommerce.model.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.repository.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String keyId;
    @Value("${razorpay.key.secret}")
    private String keySecret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
        System.out.println("PaymentService: Initialized RazorpayClient with keyId: " + keyId); // Debug log
    }

    public Payment createPayment(Long orderId, Long userId) throws RazorpayException {
        System.out.println("PaymentService: Creating payment for orderId: " + orderId + ", User ID: " + userId); // Debug log
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: ID " + userId));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: ID " + orderId));
        System.out.println("PaymentService: Found order with user_id: " + order.getUser().getId()); // Debug log
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to user: ID " + userId);
        }

        if (paymentRepository.findByOrder(order).isPresent()) {
            throw new IllegalArgumentException("Payment already exists for this order");
        }

        // Create Razorpay order (amount in paise)
        BigDecimal amountInPaise = order.getTotalAmount().multiply(new BigDecimal("100"));
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise.intValue());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_" + orderId);
        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        System.out.println("PaymentService: Created Razorpay order: " + razorpayOrder.get("id")); // Debug log

        // Save Payment entity
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus("PENDING");
        payment.setRazorpayOrderId(razorpayOrder.get("id"));
        payment.setCreatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public Payment verifyPayment(String razorpayOrderId, String paymentId, String signature) throws RazorpayException {
        System.out.println("PaymentService: Verifying payment for razorpayOrderId: " + razorpayOrderId); // Debug log
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + razorpayOrderId));
        // Simplified verification for test mode
        payment.setPaymentId(paymentId);
        payment.setStatus("PAID");
        paymentRepository.save(payment);
        Order order = payment.getOrder();
        order.setStatus("PAID");
        orderRepository.save(order);
        return payment;
    }

    public Optional<Payment> findById(Long id) {
        System.out.println("PaymentService: Fetching payment for ID: " + id); // Debug log
        return paymentRepository.findById(id);
    }
}