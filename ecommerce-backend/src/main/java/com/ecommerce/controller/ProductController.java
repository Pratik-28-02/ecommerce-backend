package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(
            @RequestPart("name") String name,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart("price") String price,
            @RequestPart("stock") String stock,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.createProduct(name, description, new BigDecimal(price), Integer.parseInt(stock), image);
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "price", required = false) String price,
            @RequestPart(value = "stock", required = false) String stock,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.updateProduct(id, name, description,
                price != null ? new BigDecimal(price) : null,
                stock != null ? Integer.parseInt(stock) : -1, image);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
