package com.grocery.system.controller;

import com.grocery.system.service.BillingService;
import com.grocery.system.entity.Bill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final BillingService billingService;

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Bill> bills = billingService.findAll();
        double totalSales = bills.stream().mapToDouble(Bill::getFinalAmount).sum();
        long totalBills = bills.size();

        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalBills", totalBills);
        
        // Profit calculation using getBillItems() instead of getItems()
        double totalProfit = bills.stream()
            .flatMap(b -> b.getBillItems().stream())
            .mapToDouble(item -> (item.getFinalPrice() - item.getProduct().getCostPrice()) * item.getQuantity())
            .sum();
            
        model.addAttribute("totalProfit", totalProfit);

        return "dashboard";
    }
}