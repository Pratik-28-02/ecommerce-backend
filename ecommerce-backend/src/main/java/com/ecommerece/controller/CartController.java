package com.ecommerece.controller;

import com.ecommerece.repository.CartRepository;
import com.ecommerece.model.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    @Autowired
    private CartRepository cartRepository;

    @PostMapping
    public Cart addTOCart(@RequestBody Cart cart) {
        return cartRepository.save(cart);
    }
}
