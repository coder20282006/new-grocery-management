package com.grocery.system.service;

import com.grocery.system.entity.Customer;
import com.grocery.system.entity.User;
import com.grocery.system.repository.CustomerRepository;
import com.grocery.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public List<Customer> findAll() { 
        return customerRepository.findByUser(getCurrentUser()); 
    }

    public Customer findById(Long id) { 
        return customerRepository.findById(id).orElse(null); 
    }

    public Customer save(Customer customer) { 
        customer.setUser(getCurrentUser());
        return customerRepository.save(customer); 
    }

    public void deleteById(Long id) { 
        customerRepository.deleteById(id); 
    }
}