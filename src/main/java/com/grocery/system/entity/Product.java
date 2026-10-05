package com.grocery.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import com.grocery.system.entity.Category;


@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    @Column(nullable = false)
    private Double costPrice;

    @Column(nullable = false)
    private Double mrp;

    @Column(nullable = false)
    private Double sellingPrice;

    @Column(nullable = false)
    private Double discount; // Percentage

    @Column(nullable = false)
    private Double finalPrice; // Auto-calculated

    @Column(nullable = false)
    private Integer quantity;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @PrePersist
    @PreUpdate
    public void calculateFinalPrice() {
        if (sellingPrice != null && discount != null) {
            this.finalPrice = this.sellingPrice - (this.sellingPrice * this.discount / 100.0);
        } else if (sellingPrice != null) {
            this.finalPrice = this.sellingPrice;
        }
    }
    

    // Tenant Isolation Reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
