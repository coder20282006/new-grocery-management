package com.grocery.system.controller;

import com.grocery.system.entity.Bill;
import com.grocery.system.entity.BillItem;
import com.grocery.system.entity.Product;
import com.grocery.system.service.BillingService;
import com.grocery.system.service.CustomerService;
import com.grocery.system.service.PdfService;
import com.grocery.system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final PdfService pdfService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bills", billingService.findAll());
        return "bills/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("customers", customerService.findAll());
        return "bills/create";
    }

    // A simple DTO for form submission
    @PostMapping("/save")
    public String save(@RequestParam(value = "customerId", required = false) Long customerId,
                       @RequestParam("productIds") List<Long> productIds,
                       @RequestParam("quantities") List<Integer> quantities,
                       org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        
        try {
            Bill bill = new Bill();
            if (customerId != null) {
                bill.setCustomer(customerService.findById(customerId));
            }

            List<BillItem> items = new ArrayList<>();
            for (int i = 0; i < productIds.size(); i++) {
                Long pid = productIds.get(i);
                Integer qty = quantities.get(i);
                if (pid != null && qty != null && qty > 0) {
                    Product p = productService.findById(pid);
                    BillItem item = new BillItem();
                    item.setProduct(p);
                    item.setQuantity(qty);
                    items.add(item);
                }
            }
            
            bill.setBillItems(items);
            Bill savedBill = billingService.createBill(bill);
            
            return "redirect:/bills/view/" + savedBill.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bills/create";
        }
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") Long id, Model model) {
        model.addAttribute("bill", billingService.findById(id));
        return "bills/view";
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<ByteArrayResource> generatePdf(@PathVariable("id") Long id) {
        Bill bill = billingService.findById(id);
        byte[] pdfBytes = pdfService.generatePdfReceipt(bill);

        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
