package com.grocery.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true) // Can be null for walk-in
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double totalDiscount;

    @Column(nullable = false)
    private Double finalAmount;

    @Column(nullable = false)
    private LocalDateTime billDate;


    @PrePersist
    public void prePersist() {
        if (billDate == null) {
            billDate = LocalDateTime.now();
        }
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillItem> billItems;
}
