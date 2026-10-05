package com.grocery.system.service;

import com.grocery.system.entity.Bill;
import com.grocery.system.entity.BillItem;
import com.grocery.system.entity.Product;
import com.grocery.system.entity.User;
import com.grocery.system.repository.BillRepository;
import com.grocery.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final ProductService productService;
    private final UserRepository userRepository;

    // Helper to fetch the currently logged-in admin
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    @Transactional
    public Bill createBill(Bill bill) {
        double subtotal = 0;
        double totalDiscount = 0;
        double finalAmount = 0;

        for (BillItem item : bill.getBillItems()) {
            // productService.findById already enforces tenant isolation
            Product product = productService.findById(item.getProduct().getId());

            // Set snapshot prices
            item.setMrp(product.getMrp());
            item.setSellingPrice(product.getSellingPrice());
            item.setDiscount(product.getDiscount());
            item.setFinalPrice(product.getFinalPrice());
            item.setBill(bill);

            // Deduct stock safely within the user's product catalog
            productService.reduceStock(product.getId(), item.getQuantity());

            // Calculate totals
            subtotal += item.getSellingPrice() * item.getQuantity();
            finalAmount += item.getFinalPrice() * item.getQuantity();
        }

        totalDiscount = subtotal - finalAmount;

        bill.setSubtotal(subtotal);
        bill.setTotalDiscount(totalDiscount);
        bill.setFinalAmount(finalAmount);
        
        // --- ASSIGN TO CURRENT TENANT ---
        bill.setUser(getCurrentUser());

        return billRepository.save(bill);
    }

    public List<Bill> findAll() {
        // Return only bills belonging to the logged-in admin
        return billRepository.findByUser(getCurrentUser());
    }

    public Bill findById(Long id) {
        // Ensure bill belongs to the logged-in admin
        return billRepository.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Bill not found or unauthorized"));
    }
}