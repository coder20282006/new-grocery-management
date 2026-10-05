package com.grocery.system.repository;

import com.grocery.system.entity.Customer;
import com.grocery.system.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByNameContainingIgnoreCase(String name);
    List<Customer> findByUser(User user);
}
