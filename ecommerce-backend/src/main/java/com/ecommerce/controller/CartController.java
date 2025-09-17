package com.ecommerce.controller;

import com.ecommerce.dto.CartRequest;
import com.ecommerce.model.Cart;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Cart> getCart(Authentication auth) {
        System.out.println("Handling GET /api/carts");
        String userId = auth.getName();
        return cartRepository.findByUser_Id(Long.parseLong(userId));
    }

    @PostMapping
    public Cart addToCart(@RequestBody @Valid CartRequest request, Authentication auth) {
        String userId = auth.getName();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + request.getProductId()));
        Cart cart = new Cart();
        User user = new User();
        user.setId(Long.parseLong(userId));
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(request.getQuantity());
        return cartRepository.save(cart);
    }
}
