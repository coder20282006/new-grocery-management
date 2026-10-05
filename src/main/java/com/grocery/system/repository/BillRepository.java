package com.grocery.system.repository;

import com.grocery.system.entity.Bill;
import com.grocery.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUser(User user);
    Optional<Bill> findByIdAndUser(Long id, User user);
}