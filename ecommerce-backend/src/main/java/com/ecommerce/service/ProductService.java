package com.ecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    Cloudinary cloudinary;

    public Product createProduct(String name, String description, BigDecimal price, int stock, MultipartFile image) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        String imageUrl = null;
        if(image != null || !image.isEmpty() ) {
            try {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(),
                ObjectUtils.emptyMap());
                imageUrl = (String) uploadResult.get("url");
            }
            catch (Exception e){
                throw new IllegalArgumentException("Failed to upload image");
            }
        }
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageUrl);
        return productRepository.save(product);
    }
    public Product updateProduct(Long id,String name,String description,BigDecimal price, int stock, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if(name != null && !name.trim().isEmpty()) {
            product.setName(name);
        }
        if(description != null){
            product.setDescription(description);
        }
        if(price != null){
            product.setPrice(price);
        }
        if(stock > 0){
            product.setStock(stock);
        }
        if(image != null && !image.isEmpty()){
            try{
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(),ObjectUtils.emptyMap());
                product.setImageUrl((String) uploadResult.get("url"));
            }
            catch (Exception e){
                throw new IllegalArgumentException("Failed to upload image");
            }
        }
        return productRepository.save(product);
    }
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }
}
