package com.grocery.system.repository;

import com.grocery.system.entity.Product;
import com.grocery.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUser(User user);
    Optional<Product> findByIdAndUser(Long id, User user);
    List<Product> findByNameContainingIgnoreCaseAndUser(String name, User user);
    
}