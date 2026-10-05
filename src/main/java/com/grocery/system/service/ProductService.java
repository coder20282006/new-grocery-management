package com.grocery.system.service;

import com.grocery.system.entity.Product;
import com.grocery.system.entity.User;
import com.grocery.system.repository.ProductRepository;
import com.grocery.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Helper to fetch currently logged-in user
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public List<Product> findAll() {
        // Return only products belonging to the logged-in admin
        return productRepository.findByUser(getCurrentUser());
    }

    public Product findById(Long id) {
        // Ensure product belongs to the logged-in admin
        return productRepository.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Product not found or unauthorized"));
    }

    public Product save(Product product) {
        if (product.getSellingPrice() > product.getMrp()) {
            throw new IllegalArgumentException("Selling price cannot be greater than MRP");
        }
        // Automatically bind product to the current tenant
        product.setUser(getCurrentUser());
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        // Fetch first to verify ownership, then delete
        Product product = findById(id);
        productRepository.delete(product);
    }

    public List<Product> search(String name) {
        // Search only within the logged-in user's products
        return productRepository.findByNameContainingIgnoreCaseAndUser(name, getCurrentUser());
    }

    public void reduceStock(Long productId, int quantity) {
        Product product = findById(productId); // Tenant-safe lookup
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock for product: " + product.getName());
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }
}